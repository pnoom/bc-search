#! /usr/bin/python

import os.path
import csv

# How to decide which SubCollection each item belongs to? For now just do
# Uncategorized?

def construct_command(row):
    command = 'INSERT INTO Item (itemRef, location, name, description, dateCreated, copyrighted, extent, subCollectionId) VALUES ({0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9});\n'

    # Normalize data here and splice in using format.

    # "Object Number","Collection Name","Geographic Name","Full Name",
    # "Scope And Content","Date","Extent","Physical/Technical","Multimedia name",
    # "Copyright","Multimedia irn"
    
    itemRef = row["Object Number"]
    location = row["Geographic Name"]
    name = row["Full Name"]
    description = row["Scope And Content"]
    dateCreated = row["Date"]
    extent = row["Extent"]
    copyrighted = row["Copyright"]
    physTechDesc = row["Physical/Technical"]
    subCollectionId = 1  #always Uncategorized, for now
    
    command.format(itemRef, location, name, description, dateCreated, copyrighted,
                   extent, subCollectionId)
    return command

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_files = ["trotter.csv", "haslam.csv", "elliott.csv"]
    output_files = ["trotter.sql", "haslam.sql", "elliott.sql"]
    input_pathnames = [os.path.join(script_pathname, ("../resources/public/" + f))
                       for f in input_files]
    output_pathnames = [os.path.join(script_pathname, ("../sql/" + f))
                        for f in output_files]
    for inf in input_pathnames, outf in output_pathnames:
        with open(inf, 'r') as input_file, open(outf,'w+') as output_file:
            csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
            for row in csv_reader:
                command = construct_command(row)
                output_file.write(command)

if __name__ == "__main__":
    run()
