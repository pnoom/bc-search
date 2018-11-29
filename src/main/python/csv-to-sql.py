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
    result = row["Collection Name"]
    assert isinstance(result, str), "Return type should be string"
    return result

# Implementation happens to be the same
def get_collection_id(row):
    return get_uncategorized_sub_id(row)

def get_sub_id_adding_if_needed(row, sub_name, subcollections):
    assert isinstance(sub_name, str), "Sub name should be string"
    if sub_name == "Uncategorized":
        result = get_uncategorized_sub_id(row)
        assert isinstance(result, int), "Return type should be int"
        return result
    else:
        #Add entry to dict, then return its id
        subcollections[sub_name] = len(subcollections.keys()) + 1
        result = subcollections[sub_name]
        assert isinstance(result, int), "Return type should be int"
        return result

def get_uncategorized_sub_id(row):
    if row["Collection Name"] == "Trotter":
        return 1
    elif row["Collection Name"] == "Haslam":
        return 2
    else:
        return 3

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

Extent stuff NOT to match:

1 negative
1 document
1 volume
1 film [Xmm]
1 pamphlet

"""
# BE PRECISE with regexes

def get_subcollection_name(row):
    # Strategy 1: extent only
    
    #extent_regexes = ["^[0-9]+ box[es]"] # Extend this a lot - see spreadsheets
    #for pattern in extent_regexes:
    #    if re.match(pattern, row["Extent"]):
    #        return row["Object Number"]

    # Strategy 2: object number only
    # eg. "2001/090/1/1", possibly followed by "/number/number/..."
    
    obj_num_regex = "^\d{4}/\d+/\d+/\d" # Modify to specify level
    match = re.match(obj_num_regex, row["Object Number"])
    if match:
        result = match.group(0)
        assert isinstance(result, str), "Return type should be string"
        return result

    # Strategy 3: check both
    
    # Catch-all
    return "Uncategorized"

# dict.get(key, default=None)
# dict.has_key(key)

def insert_subcollection(row, sub_name, collection_id):
    command = "INSERT INTO SubCollection (subCollectionRef, name, collectionId) VALUES ('{}', '{}', {});\n"
    
    for key, value in row.items():
         row[key] = value.replace(r"'", r"\'")
    
    return command.format(sub_name, row["Full Name"], get_collection_id(row))

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
                # Get subcollection name, adding it if it doesn't already exist
                # in the database
                sub_name = get_subcollection_name(row)
                sub_id = get_sub_id_adding_if_needed(row, sub_name, subcollections)
                if sub_name != "Uncategorized":
                    sub = insert_subcollection(row, sub_name, get_collection_id(row))
                    output_file.write(sub)
                
                # Should be guaranteed to have the subcollection now, so lookup
                item = insert_item(row, sub_id)
                output_file.write(item)

if __name__ == "__main__":
    run()
