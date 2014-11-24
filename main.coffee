#!/usr/bin/env node
program = require 'commander'
mime = require 'mime'
fs = require 'fs'
metaMap = require './lib/metaMap'
BPromise = require 'bluebird'

###
Command-Line-Interface:
###

program
  .version('0.1.1')
  .option('-i, --input [value]', 'Load data from disk')
  .option('-l, --list <items>','A list of input texts')
  .option('-o, --output [value]', 'Write results to file')
  .option('-d, --delim [value]','Delimiter to split text into documents')
  .option('-p, --pretty','Pretty print of JSON output')
  .parse(process.argv)


options = {}

if program.args.length > 0
  if program.args.length % 2 == 1
    throw new Error("Wrong number of supplied arguments (always pass key-value pairs)")
  else
    i = 0
    while i < program.args.length
      key = program.args[i]
      value = program.args[i+1]
      switch value
        when "false" then value = false
        when "true"  then value = true
      if Array.isArray(JSON.parse(value)) then value = JSON.parse(value)
      options[key] = value
      i += 2

extractConcepts = (corpus) ->
  res = BPromise.all(corpus.map((doc) => metaMap.getConcepts(doc, options))).then((data) =>
    console.log(data)
    outputJSON = if program.pretty then JSON.stringify(data, null, 2) else JSON.stringify(data)
    if program.output
      fs.writeFileSync(program.output, outputJSON)
    else
      console.log(outputJSON)
  )

corpus
delim = program.delim
if program.list
  delim = delim or ";"
  corpus = program.list.split(delim)
  extractConcepts(corpus)
else if (program.input)
  data = fs.readFileSync(program.input)
  mime_type = mime.lookup(program.input)
  switch mime_type
    when "text/plain"
      delim = delim or " "
      corpus = String(data).replace(/\r\n?/g, "\n").split(delim).clean("")
      extractConcepts(corpus)
    when "text/csv"
      csv.parse(String(data), (err, output) =>
        corpus = output.map( (d) => d[0] )
        extractConcepts(corpus)
      )
