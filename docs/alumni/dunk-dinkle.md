# Dunk Dinkle

<table>
    <thead>
        <tr>
            <th>Election</th>
            <th>Result</th>
        </tr>
    </thead>
    <tbody>
        
        <tr>
            <td><a href="/csm14/">CSM 14</a></td>
            <td>
                &#x1F7E2; Elected
                
                
                
                
                
            </td>
        </tr>
        
    </tbody>
</table>


## Vote Distribution

Share of ballots at each rank for each election they ran in.
The percentage is number of ballots for them divided by total number of ballots in that election.

```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {
    "url": "/data/alumni/dunk-dinkle-rank-distribution.csv",
    "format": {"type": "csv", "parse": {"Number": "number", "Rank": "number", "Votes": "number", "Share": "number"}}
  },
  "height": {"step": 24},
  "mark": {"type": "rect", "tooltip": true},
  "encoding": {
    "y": {
      "field": "Election",
      "type": "nominal",
      "title": null,
      "sort": {"op": "min", "field": "Number", "order": "descending"}
    },
    "x": {
      "field": "Rank",
      "type": "ordinal",
      "title": "Rank",
      "sort": {"op": "min", "field": "Rank", "order": "ascending"}
    },
    "color": {
      "field": "Share",
      "type": "quantitative",
      "title": "Share of ballots",
      "scale": {"type": "sqrt", "zero": true, "range": ["#ffffff", "#08306b"], "interpolate": "lab"},
      "legend": {"format": ".0%"}
    },
    "tooltip": [
      {"field": "Election", "type": "nominal"},
      {"field": "Rank", "type": "ordinal"},
      {"field": "Votes", "type": "quantitative", "title": "Ballots"},
      {"field": "Share", "type": "quantitative", "format": ".1%"}
    ]
  }
}
```

[Source data](/data/alumni/dunk-dinkle-rank-distribution.csv)


_If you would like any personal message or information on this page, contact @Kenn on the [EVE Ref Discord](https://everef.net/discord)._
