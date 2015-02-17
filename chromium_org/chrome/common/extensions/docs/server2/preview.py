#!/usr/bin/env python
# Copyright (c) 2012 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

# This helps you preview the apps and extensions docs.
#
#    ./preview.py --help
#
# There are two modes: server- and render- mode. The default is server, in which
# a webserver is started on a port (default 8000). Navigating to paths on
# http://localhost:8000, for example
#
#     http://localhost:8000/extensions/tabs.html
#
# will render the documentation for the extension tabs API.
#
# On the other hand, render mode statically renders docs to stdout. Use this
# to save the output (more convenient than needing to save the page in a
# browser), handy when uploading the docs somewhere (e.g. for a review),
# and for profiling the server. For example,
#
#    ./preview.py -r extensions/tabs.html
#
# will output the documentation for the tabs API on stdout and exit immediately.

# NOTE: RUN THIS FIRST. Or all third_party imports will fail.
import build_server
# Copy all the files necessary to run the server. These are cleaned up when the
# server quits.
build_server.main()

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import logging
import optparse
import os
import sys
import time

from local_renderer import LocalRenderer

class _RequestHandler(BaseHTTPRequestHandler):
  '''A HTTPRequestHandler that outputs the docs page generated by Handler.
  '''
  def do_GET(self):
    response = LocalRenderer.Render(self.path)
    self.send_response(response.status)
    for k, v in response.headers.iteritems():
      self.send_header(k, v)
    self.end_headers()
    self.wfile.write(response.content.ToString())

if __name__ == '__main__':
  parser = optparse.OptionParser(
      description='Runs a server to preview the extension documentation.',
      usage='usage: %prog [option]...')
  parser.add_option('-p', '--port', default='8000',
      help='port to run the server on')
  parser.add_option('-r', '--render', default='',
      help='statically render a page and print to stdout rather than starting '
           'the server, e.g. apps/storage.html. The path may optionally end '
           'with #n where n is the number of times to render the page before '
           'printing it, e.g. apps/storage.html#50, to use for profiling.')
  parser.add_option('-t', '--time', action='store_true',
      help='Print the time taken rendering rather than the result.')

  (opts, argv) = parser.parse_args()

  if opts.render:
    if opts.render.find('#') >= 0:
      (path, iterations) = opts.render.rsplit('#', 1)
      extra_iterations = int(iterations) - 1
    else:
      path = opts.render
      extra_iterations = 0

    if opts.time:
      start_time = time.time()

    response = LocalRenderer.Render(path)
    if response.status != 200:
      print('Error status: %s' % response.status)
      exit(1)

    for _ in range(extra_iterations):
      LocalRenderer.Render(path)

    if opts.time:
      print('Took %s seconds' % (time.time() - start_time))
    else:
      print(response.content.ToString())
    exit()

  print('Starting previewserver on port %s' % opts.port)
  print('')
  print('The extension documentation can be found at:')
  print('')
  print('  http://localhost:%s/extensions/' % opts.port)
  print('')
  print('The apps documentation can be found at:')
  print('')
  print('  http://localhost:%s/apps/' % opts.port)
  print('')

  logging.getLogger().setLevel(logging.INFO)
  server = HTTPServer(('', int(opts.port)), _RequestHandler)
  try:
    server.serve_forever()
  finally:
    server.socket.close()
