test

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
