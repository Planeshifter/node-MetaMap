chai = require "chai"
chaiAsPromised = require "chai-as-promised"
chai.use(chaiAsPromised)
BPromise = require "bluebird"
util = require "util"

expect = chai.expect
metaMap = require '../lib/metaMap.js'


describe "package namespace", () ->
  it "loads successfully" , () ->
    expect(metaMap.getConcepts).to.be.a("function")
    expect(metaMap.config).to.be.a("object")
