#! /usr/bin/python

import os.path
import csv

# Assumes collectionsdata.sql has run beforehand. Puts everything into their
# collection's Uncategorized subcollection for now.

def construct_command(row, subCollectionId):
    command = "INSERT INTO Item (itemRef, location, name, description, dateCreated, copyrighted, extent, physTechDesc, subCollectionId) VALUES ('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', {});\n"

    # Normalize data here and splice in using format.

    # "Object Number","Collection Name","Geographic Name","Full Name",
    # "Scope And Content","Date","Extent","Physical/Technical","Multimedia name",
    # "Copyright","Multimedia irn"

    # Need to handle single-quote escaping. Ignore "default" values for now

    for key, value in row.items():
         row[key] = value.replace(r"'", r"\'")
        
    return command.format(row["Object Number"], row["Geographic Name"], row["Full Name"],
                          row["Scope And Content"], row["Date"], row["Copyright"], row["Extent"],
                          row["Physical/Technical"], subCollectionId)

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_files = ["trotter.csv", "haslam.csv", "elliott.csv"]
    output_files = ["trotter.sql", "haslam.sql", "elliott.sql"]
    input_pathnames = [os.path.join(script_pathname, ("../resources/public/" + f))
                       for f in input_files]
    output_pathnames = [os.path.join(script_pathname, ("../sql/" + f))
                        for f in output_files]
    subCollectionId = 1
    for inf, outf in zip(input_pathnames, output_pathnames):
        with open(inf, 'r') as input_file, open(outf,'w+') as output_file:
            csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
            for row in csv_reader:
                command = construct_command(row, subCollectionId)
                output_file.write(command)
        subCollectionId += 1;

if __name__ == "__main__":
    run()
