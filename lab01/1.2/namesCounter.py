
d = dict()
with open("1.2/names.txt", "r") as f:
    for line in f:
        if line[0] not in d:
            d[line[0]] = 1
        else:
            d[line[0]] += 1

with open("1.2/names_counting.txt", "w") as f:
    for letter in  d:
        f.write("SET {} {}\n".format(letter.upper(), d[letter]))
    