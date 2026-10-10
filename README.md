# Tip Lite (BrightTipLite)

A tip calculator for the Light Phone III, built as an official Light SDK tool. It is the
Tool Library version of [BrightTip](https://github.com/gi-os/BrightTip), cut down to the
Tip mode and nothing else. It shows up on the phone as **BrightTip Lite**
(`com.gios.brighttiplite`).

Built on [lightphone/light-sdk](https://github.com/lightphone/light-sdk) (MIT). The SDK
modules (`sdk/`, `plugin/`, `lint-rules/`) are unchanged upstream code; the tool lives in
`tool/`.

## How it works

One screen, laid out like BrightTip's Tip mode:

- **Bill** at the top. Type it on the keypad. Digits push in from the right, so 1-2-3-4 is
  $12.34. There is no decimal point to get wrong. C clears, DEL removes the last digit.
- **Tip** and **Total** under it.
- **Presets**: 10, 15, 18, 20 and 22 percent. The sixth chip, OTHER, opens a page where you
  type any percent from 0 to 100 on the same keypad. Once set, that chip shows the custom
  figure.
- **Scroll wheel**: each click moves the tip percent by one point. Turning up raises it,
  turning down lowers it. It works on the custom percent page too.

The tip is taken on the bill as entered, rounded to the nearest cent. There is no tax line,
no rounding of the total and no split, because BrightTip's Tip mode has none of those.

The last tip percent you used is kept in the tool's DataStore and comes back on the next
launch. The bill does not.

All money is held in cents as `Long`, never `Double`, as in BrightTip. `asMoney()` and
`tipCentsFor()` are copied unchanged from BrightTip's `util/TipMath.kt`.

## Network and data

The tool makes no network requests and asks for no permissions. The only thing it stores is
one integer, the last tip percent.

## What was left out

BrightTip has four modes behind a picker. Giovanni asked for the tip calculator only, so the
rest was not ported:

| BrightTip feature | Why it is not here |
| --- | --- |
| Receipt mode (photograph a bill, Claude reads the items) | Needs a free camera and an Anthropic API key. The SDK only exposes the camera for QR scanning, and the Lite tool is meant to be offline. |
| Split between people, per-person totals | Out of scope for this version by request. |
| Basic calculator | Out of scope by request. |
| Currency converter | Out of scope by request, and it needs a network rate source. |
| Mode picker | Only one mode is left. |
| Receipt history and backup | Only existed for Receipt mode. |

## Build

```
./gradlew -DlightSdk.toolOnly=true :tool:assembleDebug
```

Unit tests for the money and keypad logic:

```
./gradlew -DlightSdk.toolOnly=true :tool:testDebugUnitTest
```

GitHub Actions runs tests, lint and the debug build on every push
(`.github/workflows/build-tool.yml`). It builds only; it publishes nothing.

## Tool Library submission notes

- Permissions: none. Capabilities: none.
- Network: none.
- Data at rest: one DataStore preference, the last tip percent.
- Intentionality: a single calculator screen with no feed, no refresh and no notifications.
- The wheel mapping (`WHEEL_CCW` / `WHEEL_CW`, falling back to scancodes 19 and 20 from the
  Pixart sensor) is the one the other Bright Lite tools use. It has not been checked on a
  device with this build.

## License

MIT, as the upstream SDK. See `LICENSE`.
