import sys

header = sys.argv[1]

column = 0

with open("crime_incidents_2013_CSV.csv") as csv:
    first = True
    vals = {}
    
    for line in csv:
        cols = line.split(",")

        for i, cell in enumerate(cols):
            if first and cell == header:
                column = i
            elif not first and i == column:
                vals[cell] = True

        first = False

    for val in vals.keys():
        print val
