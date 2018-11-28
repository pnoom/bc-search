#! /usr/bin/python

import os.path
import csv

def construct_command(row):
    command = 'First column: {}\n'.format(row[0])
    return command

def run():
    script_pathname = os.path.abspath(os.path.dirname(__file__))
    input_pathname = os.path.join(script_pathname,
                                  "../resources/public/sample-data.csv")
    output_pathname = os.path.join(script_pathname,
                                   "../sql/sampledata.sql")
    with open(input_pathname, 'r') as input_file, open(output_pathname, 'w+') as output_file:
        csv_reader = csv.reader(input_file, quotechar='"', delimiter=',')
        for row in csv_reader:
            command = construct_command(row)
            output_file.write(command)

if __name__ == "__main__":
    run()
