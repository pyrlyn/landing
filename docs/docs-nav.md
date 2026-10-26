# Keeping docs-nav.json in sync

`src/data/docs-nav.json` decides where each synced product doc appears in the docs sidebar.
Product repos sync their `docs/` into `content/docs/<product>/` (the `content: sync ...` commits),
so a new upstream doc lands here without a nav entry.

`tests/docs-nav.test.mjs` (run by `npm test`) fails when an English doc is neither listed in a
group nor in `exclude`. When a sync adds a doc:

- user-facing page: add `{ "file": ..., "title": ... }` to the matching group, using the doc's
  own heading, shortened the same way as its neighbours;
- internal page (plans, planned features, maintainer logs, experiments): add it to `exclude`
  with a one-line reason.

Then run `npm test`, `npm run build` and `npm run check:docs` (it reads `dist/`, so build first).
