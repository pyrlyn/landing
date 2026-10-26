// Pro wishlist email form (products the backend knows). Server render: the submit button is disabled, so
// without JS (or without API_URL) a submit can never put the address in the URL. Sends through @pyrlyn/sdk.
import { useEffect, useState, type FormEvent } from "react";
import { createClient } from "@pyrlyn/sdk";
import { wishlistError } from "../../../lib/prices";

interface Props { api?: string; project: string; title: string; describedby: string }

export default function WishlistForm({ api, project, title, describedby }: Props) {
  const [ready, setReady] = useState(false);
  const [busy, setBusy] = useState(false);
  const [status, setStatus] = useState("");
  useEffect(() => setReady(!!api), [api]);

  const submit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!api) return;
    const form = e.currentTarget;
    const email = String(new FormData(form).get("email") ?? "").trim();
    setBusy(true);
    setStatus("Sending…");
    try {
      await createClient({ url: api }).AddToWishlist({ email, project });
      // The API answers the same for a listed address, so this never says whether it was new.
      setStatus("Check your inbox: the link in the mail confirms your place.");
      form.reset();
    } catch (error) {
      setStatus(wishlistError(error));
    } finally {
      setBusy(false);
    }
  };
  return (
    <form className="wishlist" data-wishlist={project} onSubmit={submit}>
      <input className="wishlist__email" type="email" name="email" required maxLength={254} autoComplete="email" placeholder="you@example.com" aria-label={`Email for the ${title} Pro wishlist`} />
      <button className="btn btn--primary btn--block" type="submit" disabled={!ready || busy} aria-describedby={describedby}>Join the Pro wishlist</button>
      <p className="wishlist__status muted small" role="status" aria-live="polite">{status}</p>
    </form>
  );
}
