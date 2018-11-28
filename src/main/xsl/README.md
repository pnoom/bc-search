# LibreOffice Calc (spreadsheet) export filter

Given the sample spreadsheet of archive data, we want to produce XML of the form:

<archive-entry>
  <object-number>2001/090/1/1/10070</object-number>
  <collection>Trotter</collection>
  <location>Kenya</location>
  <name>Portrait of Neville Vincent and family</name>
  <description>Blah blah blah.</description>
  <date-created>28 August 1955</date-created>
  <extent>1 negative</extent>
  <phys-tech-desc></phys-tech-desc>
  <copyright></copyright>
</archive-entry>

(In a big, flat container tag).

See [here](https://stackoverflow.com/questions/32817081/convert-calcexcel-data-in-xml-in-openoffice/50507539?noredirect=1#comment88031412_50507539) and [here](https://stackoverflow.com/questions/50510017/conversion-to-xml-from-command-line) for details.
