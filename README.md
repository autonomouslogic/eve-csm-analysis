# EVE CSM Analysis

* `make process` - process all the voting data and stores the result in the `build/` directory
* `make thymeleaf` - renders the generated Markdown files into the `docs/`, which are committed
* `make mkdocs-serve` - run locally
* `make mkdocs-build` - build for deployment

## References
* [Council of Stellar Management](https://docs.everef.net/datasets/csm.html) - docs.everef.net
* [Algorithm 123 — SINGLE TRANSFERABLE VOTE BY MEEK’S METHOD](https://www.dia.govt.nz/diawebsite.NSF/Files/meekm/$file/meekm.pdf) - section 3.1 describes the `.blt` format
