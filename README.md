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




"Hello there."

length 12

Encoding the Hex 12 as a 32 bit integer

0x0000000C

then the message in bytes

Imageine Pixel 1 0xFF FF FF FF
Imagine Pixel 2 0xFF FF FF FF

Once length gets put in

New Pixel 1 0x F0 F0 F0 F0
New Pixel 2 0x F0 F0 F0 FC

To decrypt:

User selects pic from gallery
load file into bitmap
read first two pixels to get length of the message
store the results in an integer
Erorr check - make sure the size is within the size of the image
decrypt each pizel by reversing encrypt alg



private String decrypt (Bitmap picture)
{


}

