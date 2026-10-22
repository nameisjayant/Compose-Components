import { asset } from "@/lib/data";

/** The Jetpack Compose logo (public/compose-logo.png), since this is a Compose library. */
export function Logo({ size = 32, className }: { size?: number; className?: string }) {
  return (
    // eslint-disable-next-line @next/next/no-img-element -- static export, no image optimizer
    <img className={className} src={asset("/compose-logo.png")} width={size} height={size} alt="Jetpack Compose logo" />
  );
}
