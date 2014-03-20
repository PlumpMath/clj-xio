# com.lemonodor/xio

[![Build Status](https://travis-ci.org/wiseman/clj-xio.png?branch=master)](https://travis-ci.org/wiseman/clj-xio)

Extra IO functions for Clojure.


## copy

`xio/copy` is like `core/copy` but supports the `:callback` option.


## slurp

`xio/slurp` is like `core/slurp` but is buffered and supports the
`:callback` option.  2.9x faster than `core/slurp` when reading a 100
KB file, 3.4x faster when reading a 1 MB file.

## spit

`xio/spit` is like `core/spit` but is buffered and supports the
`:callback` option.


## binary-slurp

## binary-spit

## Running tests

To run unit tests:

```
$ lein test
```

To run benchmarks:

```
$ lein test :benchmark
```

## License

Copyright © 2014 John Wiseman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
