import { defineCollection } from "astro:content";
import { glob } from "astro/loaders";
import { projectSchema } from "./lib/project-schema";

// Contract: CONTENT_CONTRACT.md (and content/projects/README.md, which the sync owns).
// content/projects/README.md documents the folder and is not a project page.
const projects = defineCollection({
  loader: glob({ pattern: ["*.md", "!README.md"], base: "./content/projects" }),
  schema: projectSchema,
});

export const collections = { projects };
