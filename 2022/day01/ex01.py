import os


def parse_input(file_path):
    try:
        # adding two line breaks to assure splitlines() does not drop last number
        content = open(file_path, "r").read() + '\n\n'
    except FileNotFoundError:
        print("[WARN] Could not open", file_path)
        return None
    elf_belongings = []
    all_belongings = []
    content = content.splitlines()
    for i, line in enumerate(content):
        if line == '' or i == len(content) - 1:
            all_belongings.append(elf_belongings)
            elf_belongings = []
        else:
            elf_belongings.append(int(line))
    return all_belongings


def first_part(all_belongings):
    return max(list(map(sum, all_belongings)))


def second_part(all_belongings):
    return sum(sorted(list(map(sum, all_belongings)), reverse=True)[:3])


# --------------------Main------------------------
baseURI = os.path.abspath('')
s = os.path.sep
print('Solution first part:', first_part(parse_input(baseURI + f'{s}puzzle.txt')))
print('Solution second part:', second_part(parse_input(baseURI + f'{s}puzzle.txt')))
