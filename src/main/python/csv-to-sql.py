#! /usr/bin/python

import os.path
import csv

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

def get_id_from_pattern(row, current):
    # Best representation for patterns?
    if row["Extent"] == "34 boxes":
        return current + 1           #must do lookup here though
    # Etc
    else:
        # Uncategorized - should check this first really, before doing expensive
        # pattern matching, but need a catch-all
        if row["Collection Name"] == "Trotter":
            return 1
        elif row["Collection Name"] == "Haslam":
            return 2
        else:
            return 3

"""
def get_sub_id(row, subcollections):
    if uncategorized(row):
        if row["Collection Name"] == "Trotter":
            return 1
        elif row["Collection Name"] == "Haslam":
            return 2
        else:
            return 3
    else:
         return subcollections[row["Object Number"]]
"""

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_files = ["trotter.csv", "haslam.csv", "elliott.csv"]
    output_files = ["trotter.sql", "haslam.sql", "elliott.sql"]
    input_pathnames = [os.path.join(script_pathname, ("../resources/public/" + f))
                       for f in input_files]
    output_pathnames = [os.path.join(script_pathname, ("../sql/" + f))
                        for f in output_files]
    
    max_sub_id = 4       # 3 uncategorized ones first, so start at 4
    subcollections = {}  # (object_number, id)
    
    for inf, outf in zip(input_pathnames, output_pathnames):
        with open(inf, 'r') as input_file, open(outf,'w+') as output_file:
            csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
            for row in csv_reader:
                # if no such subcollection exists, create it
                if (not uncategorized(row) and
                    not subcollection_exists(row["Object Number"], subcollections)):
                    output_file.write(insert_subcollection(subcollections,
                                                           max_sub_id))
                    max_sub_id += 1
                # Should be guaranteed to have the subcollection now, so lookup
                output_file.write(insert_item(row, subcollections[row["Object Number"]])))

if __name__ == "__main__":
    run()
