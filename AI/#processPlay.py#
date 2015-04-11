import re
import os

with open("macbeth.txt") as playFile:
    tolerance = 1
    character_name = "MACBETH"
    failed_message = "Forsooth!"
    
    play = playFile.read()
    play = re.sub(r'\n([A-Za-z]*)\n\n',r'\1\t', play)
    play = re.sub(r'(.*)\n    (.*)',r'\1 \2', play)

    character_lines = re.findall(re.compile('[A-Za-z]*\t[^\n\t]*\n%s\t[^\n\t]*' % character_name), play)

#    print "Type\tLabel\tDescription\tText\tIf\tLearn\tGoto\tAccuracy\tMode"
#    print "output start\tout_start\t\tHello\t\tmode=none\t\t"

    prev_line_re = re.compile('    ([^\t\n]*)\n%s' % character_name)
    current_line_re = re.compile('%s\t    ([^\n\t]*)' % character_name)
    for line in character_lines:
        prev_line = re.search(prev_line_re, line).group(1)
        current_line = re.search(current_line_re, line).group(1)
        #print "inout\t\t" + prev_line + "\t" + current_line + "\t\t\t\t"+str(tolerance)+"\n"

        clean_line = ""
        for word in prev_line.split(" "):
            clean_line += re.search(r'[A-Za-z\']*', word).group(0).lower() + " "

        print clean_line
            #        print current_line
    #print "inout\t\tAnything!\t%s\t\t\t\t0\n" % failed_message
   # print macbeth
    
