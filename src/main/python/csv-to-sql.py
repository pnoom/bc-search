#! /usr/bin/python

import os.path
import csv
import re

# Assumes collectionsdata.sql has run beforehand. Doesn't convert default values
# to NULL yet.

def insert_item(row, subcollection_id):
    command = "INSERT INTO Item (itemRef, location, name, description, dateCreated, copyrighted, extent, physTechDesc, subCollectionId) VALUES ('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', {});\n"

    # Normalize data here and splice in using format.

    # "Object Number","Collection Name","Geographic Name","Full Name",
    # "Scope And Content","Date","Extent","Physical/Technical","Multimedia name",
    # "Copyright","Multimedia irn"

    # Need to handle single-quote escaping. Ignore "default" values for now

    for key, value in row.items():
         row[key] = value.replace(r"'", r"\'")
        
    return command.format(row["Object Number"], row["Geographic Name"],
                          row["Full Name"], row["Scope And Content"],
                          row["Date"], row["Copyright"], row["Extent"],
                          row["Physical/Technical"], subcollection_id)

# The "Extent" field is useful for determining subcollections ("Level" rarely
# helps, so that is omitted) but
# is inconsistently formatted. Need to keep a dict of subcollection names and
# ids. If not found, construct an extra SQL query to create that sub-
# collection, before adding the item to id. Uncategorized can be 1, 2 or 3 though.

# Need to compare both Extent and object numbers in order to determine uniqueness
# of subcollections. Actually, could use object numbers as keys to in subcollection
# dictionary! That is better than description. YES.

# When does an item belong in Uncategorized? When its Extent matches "1 negative"
# or sth, but not "x box(es)"

# In order to insert an item, need to know its subcollection.
# In order to insert a subcollection, need to know its collection.
# For the former, check "Extext" and "Object Number" against patterns and existing
# subcollections, respectively.
# For the latter, check "Collection Name".

def get_collection(row):
    return row["Collection Name"]

def get_subcollection_id(row, subcollections):
    #(adding it and corresp. id if necessary)
    name = get_subcollection_name(row, subcollections)
    if name == "Uncategorized":
        return get_uncategorized_id(get_collection(row))
    else:
        return subcollections[name]

def get_uncategorized_id(row):
    if row["Collection Name"] == "Trotter":
            return 1
    elif row["Collection Name"] == "Haslam":
        return 2
    else:
        return 3

def get_subcollection_name(row):
    regexes = ["[0-9]+ box(es)"] # Extend this a lot - see spreadsheets
    for pattern in regexes:
        if re.match(pattern, row["Extent"]):
            return row["Object Number"]
    # Catch-all
    return "Uncategorized"

# dict.get(key, default=None)
# dict.has_key(key)

"""
def create_sub_if_needed(row, subcollections, max_sub_id, output_file):
    sub = insert_subcollection(row, subcollection_id)
    output_file.write(sub)
    return max_sub_id + 1
"""

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_files = ["trotter.csv", "haslam.csv", "elliott.csv"]
    output_files = ["trotter.sql", "haslam.sql", "elliott.sql"]
    input_pathnames = [os.path.join(script_pathname, ("../resources/public/" + f))
                       for f in input_files]
    output_pathnames = [os.path.join(script_pathname, ("../sql/" + f))
                        for f in output_files]
    
    subcollections = {"Uncategorized": 0}  # 0 is placeholder, do not use value
    
    for inf, outf in zip(input_pathnames, output_pathnames):
        with open(inf, 'r') as input_file, open(outf,'w+') as output_file:
            csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
            for row in csv_reader:
                #create_sub_if_needed(row, subcollections, output_file)
                
                # Should be guaranteed to have the subcollection now, so lookup
                item = insert_item(row, subcollections[row["Object Number"]])
                output_file.write(item)

if __name__ == "__main__":
    run()
