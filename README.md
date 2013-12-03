Stegasaurus
===========

Mobile steganography app for CS4264.

Allows users to encrypt messages using images, and decrypt images created using
this app.

The app encrypts two characters of text per pixel in the "cipher-image" by
replacing the least significant bytes of each component in a pixel with a byte
from the characters, as follows:

Let `P = #00F533FF` denote a pixel, and let `M = "HI"` denote the text to be
encrypted. `"HI" = 0x4849`, so the enrcyption looks like

```
00 F5 33 FF
 4  8  4  9
04 F8 34 F9
```

So, the resulting "cipher-pixel" has the form `C = #04F834F9`.
