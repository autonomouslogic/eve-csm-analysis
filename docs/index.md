# Home

This site outlines analysis of the EVE Online CSM elections.
Raw data for the elections can be found at [EVE Ref docs](https://docs.everef.net/datasets/csm.html).
Official CSM pages are found [here](https://community.eveonline.com/community/csm/).

* [EVE Ref](https://everef.net/)
* [Discord](https://everef.net/discord)
* [Patreon](https://www.patreon.com/everef)
* [GitHub](https://github.com/autonomouslogic/eve-csm-analysis/)

## Votes
```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {"url": "data/csm-votes.csv", "format": {"type": "csv", "parse": {"CSM": "number"}}},
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
  "data": {"url": "data/csm-candidates.csv", "format": {"type": "csv", "parse": {"CSM": "number"}}},
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
