module.exports = (grunt) ->
  grunt.initConfig
    watch:
      files: [ '**/*.coffee' ]
      tasks: [ 'coffee:build']
    coffee:
      build:
        expand: true,
        cwd: 'src',
        src: [ '**/*.coffee' ],
        dest: 'lib',
        ext:  '.js'
      compileMain:
        options:
          bare: true,
        files:
          'main.js': 'main.coffee',

  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.registerTask 'default', ['coffee']
