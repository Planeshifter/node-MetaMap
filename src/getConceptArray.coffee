R = require 'ramda'

getConceptArray = ( resultSet ) ->
  return R.flatten( resultSet[0]?.Utterances?.Utterance.Phrases?.Phrase
  .map( (p) -> p.Mappings?.Mapping)
  .filter( (p) -> p != undefined )
  .map( (c) -> if Array.isArray(c) then c[0] else c )
  .map( (c) -> c.MappingCandidates?.Candidate ) )

module.exports = getConceptArray
