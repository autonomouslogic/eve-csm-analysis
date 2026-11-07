# [(${alumnus.name})]

<table>
    <thead>
        <tr>
            <th>Election</th>
            <th>Result</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="run : ${alumnus.runs}"]
        <tr>
            <td><a href="/csm[(${run.key})]/">CSM [(${run.key})]</a></td>
            <td>
                [# th:if="${ run.value } == 'elected'"]&#x1F7E2; Elected[/]
                [# th:if="${ run.value } == 'replacement'"]&#x1F7E2; Replacement[/]
                [# th:if="${ run.value } == 'picked'"]&#x1F535; CCP pick[/]
                [# th:if="${ run.value } == 'resigned'"]<span style="color: #9e9e9e;">&#x2B24;</span> Resigned[/]
                [# th:if="${ run.value } == 'eliminated'"]Eliminated[/]
                [# th:if="${ run.value } == 'withdrawn'"]Withdrawn[/]
            </td>
        </tr>
        [/]
    </tbody>
</table>

[# th:if="${hasDistribution}"]
## Vote Distribution

Share of ballots at each rank for each election they ran in.
The percentage is number of ballots for them divided by total number of ballots in that election.

```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {
    "url": "/data/alumni/[(${alumnus.slug()})]-rank-distribution.csv",
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

[Source data](/data/alumni/[(${alumnus.slug()})]-rank-distribution.csv)
[/]

_If you would like any personal message or information on this page, contact @Kenn on the [EVE Ref Discord](https://everef.net/discord)._
