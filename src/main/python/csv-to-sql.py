#! /usr/bin/python

import os.path
import csv
import re
from collections import OrderedDict

# Assumes collectionsdata.sql has run beforehand. Doesn't convert default values
# to NULL yet.

# For now, assume everything is a string, incl. the multimedia irn

def insert_item(row, subcollection_id):
    if row["Date"] == "default":
        command = "INSERT INTO item (item_ref, location, name, description, start_date, end_date, display_date, copyrighted, extent, phys_tech_desc, multimedia_irn, subcollection_id) VALUES ('{}', '{}', '{}', '{}', NULL, NULL, '{}', '{}', '{}', '{}', '{}', {});\n"
        return command.format(row["Object Number"], row["Geographic Name"],
                          row["Full Name"], row["Scope And Content"],
                          row["Date"], row["Copyright"], row["Extent"],
                          row["Physical/Technical"], row["Multimedia irn"], subcollection_id)
    else:
        command = "INSERT INTO item (item_ref, location, name, description, start_date, end_date, display_date, copyrighted, extent, phys_tech_desc, multimedia_irn, subcollection_id) VALUES ('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', {});\n"

        # Normalize data here and splice in using format.

        # "Object Number","Collection Name","Geographic Name","Full Name",
        # "Scope And Content","Date","Extent","Physical/Technical","Multimedia name",
        # "Copyright","Multimedia irn"
        return command.format(row["Object Number"], row["Geographic Name"],
                          row["Full Name"], row["Scope And Content"],
                          row["start_date"], row["end_date"],
                          row["Date"], row["Copyright"], row["Extent"],
                          row["Physical/Technical"], row["Multimedia irn"], subcollection_id)

    
def get_collection(row):
    return row["Collection Name"]

def get_collection_id(row):
    if row["Collection Name"] == "Trotter":
        return 3
    elif row["Collection Name"] == "Haslam":
        return 1
    elif row["Collection Name"] == "Elliott":
        return 2
    else:
        assert false, "Something went wrong"

def get_sub_id(row, subcollections):
    # Checks all subcollections for one which is a substring of the items object number
    for i in subcollections:
        if subcollections[i] in row["Object Number"] :
            return subcollections[i] # Return subcollection object number (Placeholder)
    return "Uncategorized reference" # Placeholder
            

"""
Extent stuff to match:

1 box
x boxes
1 file
x files
1 envelope
x envelopes (?)
1 folder
x folders
Anything like "10 folders, 3 envelopes"
x negatives
x documents
x volumes
x films
x pamphlets
x photographs
x book

Extent stuff NOT to match:

1 negative
1 document
1 film [Xmm]
1 pamphlet

"""
# BE PRECISE with regexes

def identify_subcollections(row):    
    extent_regexes = ["box(es)?", "file(s)?", "envelope(s)?",
                      "folder(s)?", "negatives", "documents",
                      "volume(s)?", "films", "pamphlets",
                      "book(s)?", "photographs"]
    col_reg = "[Cc]ollection"
    for pattern in extent_regexes:
        if (re.search(col_reg, row["Full Name"])):
            break
        if (re.search(pattern, row["Extent"])):
            return row

def insert_subcollection(row, sub_name, collection_id):
    command = "INSERT INTO subcollection (subcollection_ref, name, collection_id) VALUES ('{}', '{}', {});\n"
    return command.format("sub", row["Full Name"], get_collection_id(row))

# ---Date handlers--- (in order of decreasing regex-specificity)

def month_to_number(month):
    names = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    numbers = dict(zip(names, [x for x in range(1, 13)]))
    return numbers[month[:3]]

# "{:02d}".format(n)

# Each handler must take a list representing the non-null chunks of data captured by its regex,
# and return start_date and end_date strings in this format: 

def format_DD_Month_YYYY(dd_month_yyyy):
    dd_month_yyyy[1] = month_to_number(dd_month_yyyy[1])
    dd_month_yyyy = [int(i) for i in dd_month_yyyy]
    return "{:04d}-{:02d}-{:02d}".format(*reversed(dd_month_yyyy))

# SHOULD BE 11 HANDLERS

# 0. DD Month YYYY - DD Month YYYY
def handler0(groups):
    start_date = format_DD_Month_YYYY(groups[:3])
    end_date = format_DD_Month_YYYY(groups[3:])
    return start_date, end_date

# 1. DD Month - DD Month YYYY
def handler1(groups):
    start_date = format_DD_Month_YYYY([groups[0], groups[1], groups[-1]])
    end_date = format_DD_Month_YYYY(groups[2:])
    return start_date, end_date

# 2. DD Month YYYY
def handler2(groups):
    start_date = format_DD_Month_YYYY(groups[:3])
    end_date = format_DD_Month_YYYY(groups[:3])
    return start_date, end_date

# 3. DD-DD Month YYYY
def handler3(groups):
    start_date = format_DD_Month_YYYY([groups[0], groups[2], groups[3]])
    end_date = format_DD_Month_YYYY([groups[1], groups[2], groups[3]])
    return start_date, end_date

# 4. Month YYYY - Month YYYY
def handler4(groups):
    start_date = format_DD_Month_YYYY([1, groups[0], groups[1]])
    end_date = format_DD_Month_YYYY([28, groups[2], groups[3]])
    return start_date, end_date

# 5. Month YYYY - YYYY
def handler5(groups):
    start_date = format_DD_Month_YYYY([1, groups[0], groups[1]])
    end_date = format_DD_Month_YYYY([28, groups[0], groups[2]])
    return start_date, end_date

# 6. Month YYYY
def handler6(groups):
    start_date = format_DD_Month_YYYY([1, groups[0], groups[1]])
    end_date = format_DD_Month_YYYY([28, groups[0], groups[1]])
    return start_date, end_date

# 7. Month YY
def handler7(groups):
    groups[0] = month_to_number(groups[0])
    groups = [int(i) for i in groups]
    start_date = "{:04d}-{:02d}-{:02d}".format(groups[1]+1900, groups[0], 1)
    end_date = "{:04d}-{:02d}-{:02d}".format(groups[1]+1900, groups[0], 28)
    return start_date, end_date

# NOTE: does not consider 's'-es on RHS of range

# 8. YYYY-YYYY
def handler8(groups):
    groups = [x for x in groups if ((x != 's') and (x != 'c.'))]
    groups = [int(i) for i in groups]
    start_date = "{:04d}-{:02d}-{:02d}".format(groups[0], 1, 1)
    end_date = "{:04d}-{:02d}-{:02d}".format(groups[1], 12, 31)
    return start_date, end_date

# 9. YYYY
# if both circa and s, give or take 1 decade
# if just circa, give or take 2 years
# if just s, add 10 years
# if neither, just YYYY
def handler9(groups):
    if (('c.' in groups) and ('s' in groups)):
        groups = [x for x in groups if ((x != 's') and (x != 'c.'))]
        groups = [int(i) for i in groups]
        start_date = "{:04d}-{:02d}-{:02d}".format(groups[0]-10, 1, 1)
        end_date = "{:04d}-{:02d}-{:02d}".format(groups[0]+9, 12, 31)
    elif ('c.' in groups):
        groups = [x for x in groups if x != 'c.']
        groups = [int(i) for i in groups]
        start_date = "{:04d}-{:02d}-{:02d}".format(groups[0]-2, 1, 1)
        end_date = "{:04d}-{:02d}-{:02d}".format(groups[0]+2, 12, 31)
    elif ('s' in groups):
        groups = [x for x in groups if x != 's']
        groups = [int(i) for i in groups]
        start_date = "{:04d}-{:02d}-{:02d}".format(groups[0], 1, 1)
        end_date = "{:04d}-{:02d}-{:02d}".format(groups[0]+9, 12, 31)
    else:
        groups = [int(i) for i in groups]
        start_date = "{:04d}-{:02d}-{:02d}".format(groups[0], 1, 1)
        end_date = "{:04d}-{:02d}-{:02d}".format(groups[0], 12, 31)
    return start_date, end_date

# 10. Location DD Month Year
def handler10(groups):
    start_date, end_date = handler2(groups)
    return start_date, end_date

# 11. DD/MM/YYYY
def handler11(groups):
    groups = [int(i) for i in groups]
    start_date = "{:04d}-{:02d}-{:02d}".format(*reversed(groups))
    end_date = "{:04d}-{:02d}-{:02d}".format(*reversed(groups))
    return start_date, end_date

# ---Date handlers---

# You can add a key to a dictionary (here the row dict) by assigning a value to that key. We'll add keys for
# start_date and end_date before this function returns.

def normalize_date(row):
    D = "(?:rd|st|nd|th)?"
    C = "\[?([a-zA-Z]{2,10})\]?\s*(\d{2,4})\s*" # Month YYYY
    A = "(\d{1,2})"+D+"\s*[-]?\s*([a-zA-Z]{2,10})\s*[-]?\s*(\d{2,4})\s*" #DD Month Year (-)
    B = "\s*(c.)?\s*(\d{4})(s)?\s*" #YYYY
    
    date_regexes = ["(\d{1,2})"+D+"\s*[-]?\s*([a-zA-Z]{2,10})\s*[-]?\s*(\d{2,4})\s*[-]?\s*"+A # DD Month YYYY - DD Month YYYY
                    ,"(\d{1,2})\s*[-]?\s*([a-zA-Z]{2,10})\s*[-]?\s*"+A # DD Month - DD Month YYYY
                    ,A #DD Month YYYY
                    ,"(\d{0,2})-(\d{1,2})\s*[-]?\s*(\w{2,10})\s*[-]?\s*(\d{2,4})\s*" #DD-DD Month YYYY
                    ,C+"\s*[-]?\s*"+C # Month YYYY - Month YYYY
                    ,C+"\s*[-]?\s*"+B # Month YYYY - YYYY
                    ,C # Month YYYY
                    ,"\[?([a-zA-Z]{2,10})\]?\s*[-]?\s*(\d{2})\s*" # Month YY
                    ,"\[?"+B+"-"+B+"\]?" #YYYY-YYYY
                    ,"\[?"+B+"\]?" #YYYY
                    ,"(?:[a-zA-Z]+,\s*)"+A #Location DD Month Year
                    ,"(\d{2})(?:/)(\d{2})(?:/)(\d{4})" # DD/MM/YYYY
                    ]

    date_handlers = [handler0, handler1, handler2, handler3, handler4, handler5, handler6, handler7, handler8, handler9, handler10,handler11]
    
    regexes_and_handlers = OrderedDict(zip(date_regexes, date_handlers)) #may need to convert zip object to list first, check

    for regex, handler in regexes_and_handlers.items():
        match = re.match(regex, row["Date"])
        if match:
            filtered = [x for x in list(match.groups()) if x != None]
            # Mutates row dict so that its entries can be used in insert_item()
            row["start_date"], row["end_date"]  = handler(filtered)
            break
        else:
            row["start_date"], row["end_date"]  = "0000-00-00", "0000-00-00" #for now, defaults handled here


def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_files = ["haslam.csv", "elliott.csv", "trotter.csv"]
    output_files = ["haslam.sql", "elliott.sql", "trotter.sql"]
    input_pathnames = [os.path.join(script_pathname, ("../resources/public/" + f))
                       for f in input_files]
    output_pathnames = [os.path.join(script_pathname, ("../sql/" + f))
                        for f in output_files]
    
    subcollections = {}  # 0 is placeholder, do not use value

    for inf, outf in zip(input_pathnames, output_pathnames):
        with open(inf, 'r') as input_file, open(outf,'w+') as output_file:
            csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
            for row in csv_reader:
                # Escape single quotes in fields
                for key, value in row.items():
                    row[key] = value.replace(r"'", r"\'")
                #Want to run through identifying and adding subcollections to the SQL script. If not a subcollection run through a second time and define sub-c based on already added statements.
                if identify_subcollections(row) != None:
                    normalize_date(row)
                    subcollections[row["Full Name"]] = row["Object Number"]
                    item = insert_subcollection(row, row["Full Name"], get_collection_id(row))
                    output_file.write(item)
                                       
            input_file.seek(0)
            for row in csv_reader:
                if (row["Full Name"] not in subcollections) and (row["Full Name"] != "Full Name"):
                    normalize_date(row)
                    sub_id = get_sub_id(row,subcollections)
                    item = insert_item(row, sub_id)
                    output_file.write(item)

if __name__ == "__main__":
    run()
