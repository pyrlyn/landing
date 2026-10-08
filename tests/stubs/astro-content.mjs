export async function getCollection(name) {
  const provider = globalThis.__astroCollection;
  return provider ? provider(name) : [];
}
