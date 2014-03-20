# com.lemonodor/xio

[![Build Status](https://travis-ci.org/wiseman/clj-xio.png?branch=master)](https://travis-ci.org/wiseman/clj-xio)

Extra IO functions for Clojure.

`copy`

Like `core/copy` but supports the `:callback` option.

`slurp`

Like `core/slurp` but is buffered and supports the `:callback` option.
2.9x faster than `core/slurp` when reading a 100 KB file, 3.4x faster
when reading a 1 MB file.

`spit`

Like `core/spit` but is buffered and supports the `:callback` option.


`binary-slurp`

`binary-spit`


| `copy`         | Like `core/split` but Supports :callback. |
| `slurp`        |
| `spit`         |
| `binary-slurp` |
| `binary-spit`  |


.  Optimized `copy`, `slurp`, `spit`, `binary-slurp`
and `binary-spit`.  Each supports a `:callback` option.

## Usage

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
