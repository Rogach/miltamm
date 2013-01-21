Miltamm is a preprocessor system, that enables creation of "interactive" templates.

The idea is simple:
* You create a template (for example, some project), annotate it's files 
  with stuff similar to CPP control statements, and create definition
  of keys and file transformations in plain Scala file.
* User of template invokes `miltamm template_folder new_awesome_project`, answers
  some questions (effectively setting values for the build definition keys),
  and then miltamm generates the processed copy of template in `new_awesome_project` folder.

This preprocessor was only tested on Linux (thus would probably work on it). I haven't tested it on anything else.
