#! /usr/bin/python

import os.path
import csv

# How to decide which SubCollection each item belongs to? For now just do
# Uncategorized?

def construct_command(row):
    command = 'INSERT INTO Item (itemRef, location, name, description, dateCreated, copyrighted, extent, subCollectionId) {0};\n'
    values = []

    # Normalize data here and splice in using format. Remember *list unpacking.
    #"Object Number","Collection Name","Geographic Name","Full Name","Scope And Content","Date","Extent","Physical/Technical","Multimedia name","Copyright","Multimedia irn"
    values.append(row["Object Number")
    if row["Collection Name"] == "Trotter":
       values.append(1)
    elif row["Collection Name"] == "Haslam":
       values.append(2)
    else:
       values.append(3)
    
    return command

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_pathname = os.path.join(script_pathname,
                                  "../resources/public/sample-data.csv")
    output_pathname = os.path.join(script_pathname,
                                   "../sql/sampledata.sql")
    with open(input_pathname, 'r') as input_file, open(output_pathname, 'w+') as output_file:
        csv_reader = csv.DictReader(input_file, quotechar='"', delimiter=',')
        for row in csv_reader:
            command = construct_command(row)
            output_file.write(command)

if __name__ == "__main__":
    run()
