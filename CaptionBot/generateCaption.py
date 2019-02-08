from captionbot import CaptionBot
import sys

c = CaptionBot()
print(c.file_caption(sys.argv[1]))