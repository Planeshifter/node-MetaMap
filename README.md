[![NPM version](https://badge.fury.io/js/meta-map.svg)](http://badge.fury.io/js/meta-map)
[![build status](https://secure.travis-ci.org/Planeshifter/node-MetaMap.png)](http://travis-ci.org/Planeshifter/node-MetaMap)

MetaMap
=================

This little package provides an easy access point to the [MetaMap Web API] (http://ii.nlm.nih.gov/Web_API/index.shtml), which allows analyzing textual documents and mapping them to concepts found in the Unified Medical Language System (UMLS) Metathesaurus.

## Requirements & Installation

The following pre-requisites have to be fulfilled before it is possible to use the package: 
- The package requires a running installation of JAVA version 1.6.0 
- The `$JAVA_HOME` environment variable has to be specified correctly 
- The `config.json` file should include the username, email address and password of the UMLS account used to perform the MetaMap Web API queries. If not specified, it is possible to assign the correct values at run-time.

The package itself can be conveniently installed via npm:

```
npm install meta-map
```

## Basic Usage

Require the package as usual via

```
var metaMap = require("meta-map");   
```

The package exposes one function with the following arguments:

### getConcepts(docs, [options], [callback])

`docs` should be either a single String or an Array of Strings, holding the documents to be analyzed. The second (optional) parameter of the function expects an object specifying the options used in data processing. Finally, a callback function should be passed to the function which by node.js convention receives two arguments, `err` and `data` and is called once the API call has returned. As an alternative to callbacks, Promises are supported using the `Bluebird` package.

#### Example:
Using callbacks: 

```
metaMap.getConcepts(docs, null, function(err, data){
  console.log(data);
})
```

And using promises:

```
metaMap.getConcepts(docs).then(console.log)
```

#### Options 

Most of the options which can be specified are equal one-to-one to the options which can be specified in the [interactive MetaMap tool](http://ii.nlm.nih.gov/Interactive/UTS_Required/metamap.shtml), with the Output/Display options missing as all output is returned in JSON format in the node-MetaMap package.

The following Boolean options of the MetaMap API are supported:

- Composite Phrases (Q)
- No Text Tagging (t)
- No Derivational Variants (d)
- All Derivational Variants (D)
- Allow Acronym/Abbreviation Variants (a)
- Unique Acronym/Abbreviation Variants Only (u)
- Ignore Stop Phrases (K) 
- Allow Large N (l)
- Threshold (r):
- Ignore Word Order (i)
- Prefer Multiple Concepts (Y)
- Compute/Display All Mappings (b)
- Use Word Sense Disambiguation (y)

Each of these can be activated by adding a key-value pair to the options object which has the value `true`, e.g.
```
options = {
  Q: true,
  t: true,
  r: true
}
```

Source vocabularies can be excluded using the `R` and `e` keys, which specify a set of vocabularies to which the search should be restricted or which should be excluded from the data processing, respectively. They both expect a string array of vocabularies, e.g.
```
options = {r: ["AIR","AOD"]}
```

Similarly, restriction or exclusion of Semantic Type(s) is possible via the options `J` and  `k`, which again should both be arrays of the chosen types. 

### config
This exposed object holds the user data necessary to connect to the MetaMap Web API. By default, it will load the contents of the `config.json` file in the project directory. Values can also be assigned at run-time by simple assignment:

```
metaMap.config = { username: "my username", password: "my password", email: "my email" }
```

## Command-Line-Interface (CLI)

In addition to the node.js package, a CLI is provided which can be used from the terminal after installing the package globally:

```
npm install MetaMap -g 
```

Help on how to use it can then be obtained from the terminal via the command 

```
MetaMap --help
```

### MetaMap Web API
Version 2.1, June 1, 2012

1. Introduction
The following Terms and Conditions apply for use of MetaMap and associated MetaMap Tools. Using MetaMap and MetaMap Tools indicates your acceptance of the following Terms and Conditions. These Terms and Conditions apply to all MetaMap and MetaMap Tools components, independent of format and method of acquisition.

Users of the data distributed with MetaMap and MetaMap Tools are responsible for compliance with the UMLS Metathesaurus License Agreement which requires you to respect the copyrights of the constituent vocabularies and to file a brief annual report on your use of the UMLS. You also must have activated a UMLS Terminology Services (UTS) account.

2. Availability
MetaMap and MetaMap Tools are available to all requesters, both within and outside the United States, at no charge.

3. Use of MetaMap and MetaMap Tools
    Redistributions of MetaMap and MetaMap Tools in source or binary form must include this list of conditions in the documentation and other materials provided with the distribution.
    In any publication or distribution of all or any portion of MetaMap and MetaMap Tools 
(1) you must attribute the source of the tools as MetaMap and MetaMap Tools with the release number and date; 
(2) you must clearly annotate within the source code, any modification made to MetaMap and MetaMap Tools; and 
(3) any subsequent distribution of program, tool, or material based on MetaMap and MetaMap Tools, must be accomplished within the context of an open source set of terms and conditions such as the GNU General License.
    Bugs, questions, issues relating to MetaMap and MetaMap Tools should be directed to the most recent of the chain of entities that may have modified and re-distributed this code.
    You shall not assert any proprietary rights to any portion of MetaMap and MetaMap Tools, nor represent MetaMap and MetaMap Tools or any part thereof to anyone as other than a United States Government product.
    The name of the U.S. Department of Health and Human Services, National Institutes of Health, National Library of Medicine, and Lister Hill National Center for Biomedical Communications may not be used to endorse or promote products derived from MetaMap and MetaMap Tools without specific prior written permission.
    Neither the United States Government, U.S. Department of Health and Human Services, National Institutes of Health, National Library of Medicine, Lister Hill National Center for Biomedical Communications, nor any of its agencies, contractors, subcontractors or employees of the United States Government make any warranties, expressed or implied, with respect to MetaMap and MetaMap Tools, and, furthermore, assume no liability for any party's use, or the results of such use, of any part of these tools.

These terms and conditions are in effect as long as the user retains any part of MetaMap and MetaMap Tools.
