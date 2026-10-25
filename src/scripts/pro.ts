import { createClient } from "@pyrlyn/sdk";
import { fetchPrices, wishlistError } from "../lib/prices";

// Progressive enhancement: the build already rendered the API's prices; this catches up with price
// changes since the build and runs the wishlist forms. No API URL in the page = nothing to do.
const url = document.body.dataset.api;

async function refreshPrices(api: string) {
  const nodes = document.querySelectorAll<HTMLElement>("[data-pro-price]");
  if (!nodes.length) return;
  try {
    const prices = await fetchPrices(api);
    for (const node of nodes) {
      const price = prices.get(node.dataset.proPrice ?? "");
      if (price) node.textContent = price;
    }
  } catch (error) {
    console.warn("pro: price refresh failed, build-time prices stay", error);
  }
}

// The submit buttons ship disabled: without this script a submit would put the address in the URL.
function initWishlistForms(api: string) {
  const client = createClient({ url: api });
  for (const form of document.querySelectorAll<HTMLFormElement>("form[data-wishlist]")) {
    const status = form.querySelector<HTMLElement>("[role=status]");
    const button = form.querySelector<HTMLButtonElement>("button[type=submit]");
    if (!button) continue;
    const say = (text: string) => {
      if (status) status.textContent = text;
    };
    button.disabled = false;
    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const email = String(new FormData(form).get("email") ?? "").trim();
      button.disabled = true;
      say("Sending…");
      try {
        await client.AddToWishlist({ email, project: form.dataset.wishlist ?? "" });
        // The API answers the same for a listed address, so this never says whether it was new.
        say("Check your inbox: the link in the mail confirms your place.");
        form.reset();
      } catch (error) {
        say(wishlistError(error));
      } finally {
        button.disabled = false;
      }
    });
  }
}

/** The confirm and unsubscribe pages the API's mails link to; each acts only on a click. */
function initWishlistLink(api: string) {
  const box = document.querySelector<HTMLElement>("[data-wishlist-link]");
  const button = box?.querySelector<HTMLButtonElement>("button");
  if (!box || !button) return;
  const status = box.querySelector<HTMLElement>("[role=status]");
  const say = (text: string) => {
    if (status) status.textContent = text;
  };
  const token = new URLSearchParams(location.search).get("token") ?? "";
  // Drop the token from the address bar so it is not copied, bookmarked or shared.
  if (location.search) history.replaceState(null, "", location.pathname);
  if (!token) {
    say("This link is incomplete. Open the link from the mail again.");
    return;
  }
  const client = createClient({ url: api });
  button.disabled = false;
  button.addEventListener("click", async () => {
    button.disabled = true;
    say("Sending…");
    try {
      if (box.dataset.wishlistLink === "confirm") {
        const { confirmWishlist } = await client.ConfirmWishlist({ token });
        say(
          confirmWishlist?.__typename === "MutationConfirmWishlistSuccess"
            ? `Confirmed: you are on the ${confirmWishlist.data} Pro wishlist.`
            : "This link is invalid, already used or expired.",
        );
      } else {
        await client.UnsubscribeWishlist({ token });
        say("Unsubscribed: no more mail about this wishlist.");
      }
    } catch (error) {
      say(wishlistError(error));
      button.disabled = false;
    }
  });
}

if (url) {
  void refreshPrices(url);
  initWishlistForms(url);
  initWishlistLink(url);
}
