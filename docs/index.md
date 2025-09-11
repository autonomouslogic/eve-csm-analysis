# Home

This site outlines analysies of the EVE Online CSM elections.
Raw data for the elections can be found at [EVE Ref docs](https://docs.everef.net/datasets/csm.html).

* [EVE Ref](https://everef.net/)
* [Discord](https://everef.net/discord)
* [Patreon](https://www.patreon.com/everef)
* [GitHub](https://github.com/autonomouslogic/eve-csm-analysis/)

## Votes
```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {"url": "data/csm-votes.csv"},
  "mark": {
    "type": "bar",
    "point": true
  },
  "encoding": {
    "x": {"field": "CSM", "type": "nominal"},
    "y": {"field": "Votes", "type": "quantitative"}
  }
}
```
[Source data](data/csm-votes.csv)

## Candidates
```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {"url": "data/csm-candidates.csv"},
  "mark": {
    "type": "bar",
    "point": true
  },
  "encoding": {
    "x": {"field": "CSM", "type": "nominal"},
    "y": {"field": "Candidates", "type": "quantitative"}
  }
}
```
[Source data](data/csm-candidates.csv)
