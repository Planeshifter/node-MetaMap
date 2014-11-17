/*
Example abstract taken from
  http://www.nature.com/ki/journal/v67/n6/abs/4495286a.html
*/

var metaMap = require("../lib/metaMap.js");
var util = require("util");

var abstract = 'Definition and classification of chronic kidney disease: A position statement from Kidney Disease: Improving Global Outcomes (KDIGO). Chronic kidney disease (CKD) is a worldwide public health problem, with adverse outcomes of kidney failure, cardiovascular disease (CVD), and premature death. A simple definition and classification of kidney disease is necessary for international development and implementation of clinical practice guidelines. Kidney Disease: Improving Global Outcomes (KDIGO) conducted a survey and sponsored a controversies conference to (1) provide a clear understanding to both the nephrology and nonnephrology communities of the evidence base for the definition and classification recommended by Kidney Disease Quality Outcome Initiative (K/DOQI), (2) develop global consensus for the adoption of a simple definition and classification system, and (3) identify a collaborative research agenda and plan that would improve the evidence base and facilitate implementation of the definition and classification of CKD.'

metaMap.getConcepts(abstract, {y: true, e: ['AOT','HGNC']}, function(err, data){
  console.log(util.inspect(data, null, 6));
})
