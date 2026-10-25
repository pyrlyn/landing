// Confirm / unsubscribe pages the API's mails link to. The one-time `?token=` is read after hydration and
// dropped from the address bar; it is sent only on a click, so a mail scanner opening the link changes
// nothing. Server render: disabled button, status "not available" when the build has no API.
import { useEffect, useState } from "react";
import { createClient } from "@pyrlyn/sdk";
import { wishlistError } from "../../../lib/prices";

interface Props { api?: string; action: "confirm" | "unsubscribe"; label: string }

export default function WishlistLink({ api, action, label }: Props) {
  const [token, setToken] = useState("");
  const [enabled, setEnabled] = useState(false);
  const [status, setStatus] = useState(api ? "" : "The wishlist is not available on this site yet.");

  useEffect(() => {
    if (!api) return;
    const t = new URLSearchParams(location.search).get("token") ?? "";
    // Drop the token from the address bar so it is not copied, bookmarked or shared.
    if (location.search) history.replaceState(null, "", location.pathname);
    if (!t) { setStatus("This link is incomplete. Open the link from the mail again."); return; }
    setToken(t); setEnabled(true);
  }, [api]);

  const click = async () => {
    if (!api || !token) return;
    const client = createClient({ url: api });
    setEnabled(false);
    setStatus("Sending…");
    try {
      if (action === "confirm") {
        const { confirmWishlist } = await client.ConfirmWishlist({ token });
        setStatus(confirmWishlist?.__typename === "MutationConfirmWishlistSuccess"
          ? `Confirmed: you are on the ${confirmWishlist.data} Pro wishlist.`
          : "This link is invalid, already used or expired.");
      } else {
        await client.UnsubscribeWishlist({ token });
        setStatus("Unsubscribed: no more mail about this wishlist.");
      }
    } catch (error) {
      setStatus(wishlistError(error));
      setEnabled(true);
    }
  };
  return (
    <>
      <button className="btn btn--primary btn--lg" type="button" disabled={!enabled} onClick={click}>{label}</button>
      <p className="err__text" role="status" aria-live="polite">{status}</p>
    </>
  );
}
