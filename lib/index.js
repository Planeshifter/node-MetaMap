(function() {
  module.exports = {
    getConceptArray: require('./getConceptArray.coffee', {
      config: require('./metaMap.coffee').config,
      getConcepts: require('./metaMap.coffee').getConcepts
    })
  };

}).call(this);
