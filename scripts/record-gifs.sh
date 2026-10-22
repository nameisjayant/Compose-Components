#!/usr/bin/env bash
# Renders every component scene off-device (Robolectric) and turns the frames into the README GIFs
# in docs/gifs/. Needs ffmpeg on the PATH. Pass a test name (e.g. ratingBar) to record just that one.
set -euo pipefail

cd "$(dirname "$0")/.."
frames="$PWD/app/build/gif-frames"
out="$PWD/docs/gifs"
mkdir -p "$out"

filter="*.RecordComponentGifs${1:+.$1}"
./gradlew :app:testDebugUnitTest --tests "$filter" -PgifDir="$frames" --rerun

for dir in "$frames"/*/; do
  name="$(basename "$dir")"
  ffmpeg -loglevel error -y -framerate 25 -i "$dir/frame_%04d.png" \
    -vf "scale=540:-1:flags=lanczos,split[a][b];[a]palettegen=stats_mode=diff[p];[b][p]paletteuse=dither=bayer:bayer_scale=5:diff_mode=rectangle" \
    -loop 0 "$out/$name.gif"
  echo "docs/gifs/$name.gif"
done
