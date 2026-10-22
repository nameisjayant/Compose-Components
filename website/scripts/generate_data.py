#!/usr/bin/env python3
"""Builds src/data/components.json and public/gifs/ from the ComposeComponents repo (this site's parent folder).

Run it again whenever a component is added to (or changed in) the library:

    npm run sync

Usage text, code, GIFs and parameter tables come from the README's "## <emoji> ComposeXxx"
sections; signatures come from the library sources. The short descriptions, tags and the
"How it works" notes for each component live in COMPONENTS below, since the README doesn't
have them. A component that is in the README but not in COMPONENTS fails the sync, so a new
one can't be published half-described.
"""
import json
import re
import shutil
import sys
from pathlib import Path

SITE = Path(__file__).resolve().parent.parent
SOURCES = "components/src/main/java/com/nameisjayant/components"

# Order here is the sidebar order. `apis` are the public functions whose signatures go in the API section.
COMPONENTS = [
    {
        "composable": "ComposeDatePicker",
        "slug": "date-picker",
        "name": "Date picker",
        "package": "datepicker",
        "card": "Single date, range or many dates, with disabled days, decorations and a year grid.",
        "tags": ["State-driven", "Single · Range · Multiple", "Dialog", "minSdk 24, no desugaring"],
        "apis": ["ComposeDatePicker", "rememberComposeDatePickerState", "ComposeDatePickerDialog"],
        "concepts": [
            "**The state holds everything.** `rememberComposeDatePickerState()` keeps the selection mode, the selected date(s), the displayed month and the rules for which dates can be picked. The composable only draws it, so you read results from the state (`selectedDate`, `selectedRange`, `selectedDates`) and can drive it from code.",
            "**Three selection modes.** `DateSelectionMode.Single` picks one date, `Range` picks a start and an end (first tap, second tap), and `Multiple` toggles any number of dates, optionally capped with `maxSelectionCount`.",
            "**Rules decide what can be tapped.** `minDate` / `maxDate` are hard bounds the calendar won't page past. `SelectableDates` rules (weekdays only, except a list, your own lambda) disable individual days, and they combine with `and` / `or`.",
            "**Decorate or redraw days.** `decoration` adds dots, a small label or a tint to chosen days. `dayContent` replaces what's drawn inside a day, while the picker still draws the selection, today's outline and the range band.",
            "**Its own date types.** `CalendarDate` and `CalendarMonth` replace `java.time`, so the picker runs on minSdk 24 without core library desugaring.",
        ],
    },
    {
        "composable": "ComposeWheelPicker",
        "slug": "wheel-picker",
        "name": "Wheel picker",
        "package": "wheelpicker",
        "card": "A scrolling wheel that snaps to a row. Cyclic, flat or curved, with custom rows.",
        "tags": ["State or value", "Snap fling", "Cyclic", "Haptics"],
        "apis": ["ComposeWheelPicker", "rememberComposeWheelPickerState"],
        "concepts": [
            "**Two ways to hold the selection.** Pass `selectedIndex` + `onSelectedIndexChange` when you only need the final value (the callback runs once the wheel comes to rest). Use `rememberComposeWheelPickerState()` when you want the index live while it spins, or want to scroll it from code with `animateScrollToIndex` / `scrollToIndex`.",
            "**It always lands on a row.** A drag or fling decays and snaps so one row rests on the selection line; tapping a row spins to it. Each row that passes the line gives a light haptic tick.",
            "**Endless lists.** `cyclic = true` wraps the list so it spins in both directions forever, and scrolling from code takes the short way round.",
            "**Lists can change while shown.** If `items` changes (31 days becoming 28), the wheel keeps its item, or moves to the last one if the list got shorter. `isItemEnabled` dims rows and the wheel skips over them.",
            "**Looks.** `WheelPickerStyle.Wheel` curves rows like a drum, `Flat` shrinks and fades them. The `selectionIndicator` slot draws behind the selected row and can be turned off so several wheels share one band.",
        ],
    },
    {
        "composable": "ComposeOtpInput",
        "slug": "otp-input",
        "name": "OTP input",
        "package": "otpinput",
        "card": "A one-time-code field split into cells, with paste, SMS autofill, masking and an error shake.",
        "tags": ["Value + callback", "Paste & SMS autofill", "PIN masking", "Error shake"],
        "apis": ["ComposeOtpInput"],
        "concepts": [
            "**One hidden text field behind the cells.** The cells are drawn from a single `value` string, so you keep the code in your own state and update it from `onValueChange`. The cursor always sits at the end of the code.",
            "**Pasting just works.** Pasting a full code, or an SMS autofill, replaces what's there. Characters the input doesn't accept, like the dash in `529-174`, are dropped, so the code still fills the cells.",
            "**`onComplete` fires once** when the last cell is filled, and the keyboard closes (`clearFocusOnComplete`). That's where you verify the code.",
            "**Errors are visible and felt.** Set `isError = true` and the cells turn red, the row shakes and the phone buzzes once. Reset it in `onValueChange` when the user edits.",
            "**Shape it to your screen.** Choose `Outlined`, `Filled` or `Underlined` cells, split them into groups with a separator, mask a PIN, accept letters or your own `charFilter`, or draw each cell yourself with `cellContent`.",
        ],
    },
    {
        "composable": "ComposeRatingBar",
        "slug": "rating-bar",
        "name": "Rating bar",
        "package": "ratingbar",
        "card": "Stars, hearts or any shape. Tap or drag to rate, with halves, read-only mode and a fill cascade.",
        "tags": ["Value + callback", "Tap & drag", "Any Shape or Painter", "Animated"],
        "apis": ["ComposeRatingBar"],
        "concepts": [
            "**Stateless.** You pass `rating` and update it in `onRatingChange`, which runs while the user taps or drags. `onRatingChangeFinished` runs when the finger lifts, which is the place to save.",
            "**Read-only is `onRatingChange = null`.** The bar then just shows the value, and fractional ratings such as `3.7f` draw a partly filled item.",
            "**`stepSize` rounds the input.** `1f` for whole items, `0.5f` for halves, `0f` for any value. `minRating` stops the user going below a floor and `allowClear` decides whether tapping the current rating resets it.",
            "**Any item.** Use the built-in `StarShape`, `SharpStarShape` and `HeartShape`, build a star with `starShape(points, innerRadiusRatio, roundness)`, pass any Compose `Shape`, or a `Painter` that gets tinted with the bar's colors.",
            "**Animation runs in the draw phase.** Items fill one after another, the tapped one pops with a halo and the one under your finger lifts, without recomposing the bar. Tune it with `RatingBarAnimation(...)` or turn it off with `.None`.",
        ],
    },
    {
        "composable": "ComposeTimeline",
        "slug": "timeline",
        "name": "Timeline",
        "package": "timeline",
        "card": "Steps joined by a line, vertical or horizontal: order tracking, steppers, event history.",
        "tags": ["Generic items", "Vertical & horizontal", "LazyColumn support", "Animated line"],
        "apis": ["ComposeTimeline", "timelineItems", "StatusIndicator", "NumberIndicator", "IconIndicator"],
        "concepts": [
            "**You give items, it gives steps.** `items` can be any type. For each one, `content` (and optional `oppositeContent`) receives a `TimelineStep` with the item, its `index`, its `state` and where it sits (`isFirst`, `isLast`, `contentSide`).",
            "**Each step has a state.** `stepState` returns `Completed`, `Active` (the indicator pulses), `Upcoming` or `Error`. `TimelineStepState.forProgress(index, currentIndex)` covers the usual case: steps before the current one are done, the current one is active, later ones upcoming.",
            "**The line follows progress.** It's drawn as reached up to the furthest reached step and in `pendingLineStyle` after it. When `currentStep` moves forward, the line animates into the next step.",
            "**Indicators are a slot.** The default `StatusIndicator` draws a ring, pulsing dot, check or `!`. Swap in `NumberIndicator`, `IconIndicator` or your own composable; the slot's scope gives you the colors, `indicatorSize` and animation.",
            "**Short or long.** `ComposeTimeline` lays out every step at once (vertical or horizontal). For long vertical lists, `timelineItems(...)` adds the steps straight into a `LazyColumn`.",
        ],
    },
    {
        "composable": "ComposeColorPicker",
        "slug": "color-picker",
        "name": "Color picker",
        "package": "colorpicker",
        "card": "Wheel, ring or square HSV picker with alpha, before/after preview, hex field and swatches.",
        "tags": ["State or value", "Wheel · Ring · Square", "HSV, keeps hue", "Composable parts"],
        "apis": ["ComposeColorPicker", "rememberComposeColorPickerState", "ComposeColorPickerDialog"],
        "concepts": [
            "**Works in HSV.** The picker stores hue, saturation, value and alpha (`HsvColor`) rather than a `Color`, so dragging down to gray or black and back up keeps the hue you had.",
            "**Two ways to hold the color.** `color` + `onColorChange` for a plain value, or `rememberComposeColorPickerState()` when you want to read and set `state.color` / `state.hsvColor` at any time. `onColorChangeFinished` runs when a drag ends, a swatch is tapped or a hex code is entered.",
            "**Three styles for the main area.** `Wheel` is a hue/saturation disc with a brightness slider, `Ring` is a hue ring around a saturation/brightness square, and `Square` is a panel with a hue slider.",
            "**Toggle the extras.** Alpha slider, before/after preview (`originalColor`, tap the old half to go back), hex field and swatches can each be shown or hidden.",
            "**Or build your own.** Every part (`SaturationValuePanel`, `HueSlider`, `AlphaSlider`, `ColorHexField`, `ColorSwatches`…) is public and stateless, taking `hsvColor` + `onHsvColorChange`, so you can lay them out however you like.",
        ],
    },
]


def sections(body: str, level: str) -> list[tuple[str | None, str]]:
    """Splits text into [(heading, body)] at headings of exactly `level` (e.g. "##"), ignoring code.
    The first entry holds the text before the first heading, with a None heading."""
    out, title, buf, in_code = [], None, [], False
    pattern = re.compile(rf"^{level} (?!#)")
    for line in body.splitlines():
        if line.startswith("```"):
            in_code = not in_code
        if not in_code and pattern.match(line):
            out.append((title, "\n".join(buf).strip()))
            title, buf = line[len(level) + 1:].strip(), []
        else:
            buf.append(line)
    out.append((title, "\n".join(buf).strip()))
    return out


def html_to_md(text: str) -> str:
    text = re.sub(r"</?code>", "`", text)
    text = re.sub(r"</?b>", "**", text)
    return re.sub(r"<[^>]+>", "", text).strip()


def blocks(body: str) -> list[dict]:
    """Splits a section body into paragraphs, subheadings, code blocks and tables. HTML lines are skipped."""
    out, lines, i = [], body.splitlines(), 0
    while i < len(lines):
        line = lines[i]
        if line.startswith("```"):
            lang, code, i = line[3:].strip() or "text", [], i + 1
            while not lines[i].startswith("```"):
                code.append(lines[i])
                i += 1
            out.append({"type": "code", "lang": lang, "code": "\n".join(code)})
        elif line.startswith("|"):
            rows = []
            while i < len(lines) and lines[i].startswith("|"):
                cells = [c.strip() for c in lines[i].strip().strip("|").split("|")]
                if not all(re.fullmatch(r"-+", c) for c in cells):
                    rows.append(cells)
                i += 1
            out.append({"type": "table", "head": rows[0], "rows": rows[1:]})
            continue
        elif re.fullmatch(r"\*\*`[^*`]+`\*\*", line.strip()):
            out.append({"type": "h", "text": line.strip().strip("*")})
        elif line.strip() and not line.startswith("<") and line.strip() != "---":
            para = [line.strip()]
            while i + 1 < len(lines) and lines[i + 1].strip() and not re.match(r"^(```|\||<|#|---)", lines[i + 1]):
                i += 1
                para.append(lines[i].strip())
            out.append({"type": "p", "text": " ".join(para)})
        i += 1
    return out


def slugify(title: str) -> str:
    return re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")


def gifs(body: str) -> list[dict]:
    """The README's <img> GIFs, each with its <sub> caption (or its alt text when there is none)."""
    out = []
    for cell in re.split(r"</td>|</p>", body):
        m = re.search(r'<img src="docs/gifs/([\w-]+)\.gif"[^>]*alt="([^"]*)"', cell)
        if m:
            sub = re.search(r"<sub>(.*?)</sub>", cell)
            out.append({"file": m.group(1), "alt": m.group(2), "caption": html_to_md(sub.group(1)) if sub else m.group(2)})
    return out


def signatures(repo: Path, package: str, names: list[str]) -> str:
    """Every public overload of `names` in the package, without bodies or KDoc."""
    found: dict[str, list[str]] = {n: [] for n in names}
    for f in sorted((repo / SOURCES / package).glob("*.kt")):
        lines = f.read_text().splitlines()
        for i, line in enumerate(lines):
            m = re.match(r"^public fun (?:<[^>]+> )?(?:[\w.]+\.)?(\w+)\(", line)
            if not m or m.group(1) not in found:
                continue
            # The parameter list ends where the parentheses opened on the first line balance out.
            j, depth = i, 0
            while True:
                depth += lines[j].count("(") - lines[j].count(")")
                if depth <= 0:
                    break
                j += 1
            sig = lines[i: j + 1]
            sig[-1] = re.sub(r"\s*\{\s*$|\s+=\s.*$", "", sig[-1])
            ann = "@Composable\n" if i > 0 and lines[i - 1].strip() == "@Composable" else ""
            found[m.group(1)].append(ann + "\n".join(sig))
    missing = [n for n, s in found.items() if not s]
    if missing:
        sys.exit(f"No public signature found for {missing} in {package}")
    return "\n\n".join(s for n in names for s in found[n])


def main() -> None:
    repo = Path(sys.argv[1]).expanduser() if len(sys.argv) > 1 else SITE.parent
    readme = (repo / "README.md").read_text()
    props = (repo / "gradle.properties").read_text()
    version = re.search(r"^VERSION_NAME=(.+)$", props, re.M).group(1).strip().removesuffix("-SNAPSHOT")

    by_name = {re.sub(r"^\W+\s*", "", t): b for t, b in sections(readme, "##") if t}
    known = {c["composable"] for c in COMPONENTS}
    unknown = [n for n in by_name if n.startswith("Compose") and n not in known]
    if unknown:
        sys.exit(f"README has components missing from COMPONENTS in generate_data.py: {unknown}")

    install_parts = sections(by_name["Installation"], "###")
    install_main = install_parts[0][1]
    catalog = re.search(r"<details>(.*?)</details>", install_main, re.S).group(1)
    guide = {
        "installation": blocks(re.sub(r"<details>.*?</details>", "", install_main, flags=re.S)),
        "catalog": blocks(catalog),
        "conventions": blocks(dict(install_parts[1:])["Shared conventions"]),
    }

    comps = []
    gif_files = set()
    for meta in COMPONENTS:
        body = by_name.get(meta["composable"])
        if body is None:
            sys.exit(f"README has no '## … {meta['composable']}' section")
        parts = sections(body, "###")
        head = parts[0][1]
        details = ""
        subs = []
        for title, text in parts[1:]:
            d = re.search(r"<details>(.*?)</details>", text, re.S)
            if d:
                details = re.sub(r"<summary>.*?</summary>", "", d.group(1), flags=re.S)
                text = text[: d.start()]
            subs.append({"id": slugify(title), "title": title, "blocks": blocks(text)})
        g = gifs(head)
        gif_files.update(x["file"] for x in g)
        comps.append({
            **{k: meta[k] for k in ("composable", "slug", "name", "card", "tags", "concepts")},
            "package": f"com.nameisjayant.components.{meta['package']}",
            "summary": next(b["text"] for b in blocks(head) if b["type"] == "p"),
            "gifs": g,
            "sections": subs,
            "params": blocks(details),
            "signature": signatures(repo, meta["package"], meta["apis"]),
        })

    data = {"version": version, "guide": guide, "components": comps}
    out = SITE / "src/data/components.json"
    out.parent.mkdir(parents=True, exist_ok=True)
    # The README says <version>; the site shows the real one.
    out.write_text(json.dumps(data, indent=2, ensure_ascii=False).replace("<version>", version) + "\n")

    dest = SITE / "public/gifs"
    shutil.rmtree(dest, ignore_errors=True)
    dest.mkdir(parents=True)
    for name in sorted(gif_files):
        shutil.copy(repo / "docs/gifs" / f"{name}.gif", dest / f"{name}.gif")
    print(f"Wrote {len(comps)} components (v{version}), {len(gif_files)} GIFs → {out.relative_to(SITE)}")


if __name__ == "__main__":
    main()
