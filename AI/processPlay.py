import re
import os

with open("macbeth.txt") as playFile:
    macbeth = playFile.read()
    macbeth = re.sub(r'\n([A-Za-z]*)\n\n',r'\1\t', macbeth)
    macbeth = re.sub(r'(.*)\n    (.*)',r'\1 \2', macbeth)
    print macbeth
    
