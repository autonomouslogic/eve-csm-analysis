# CSM [(${analysis.csmNumber})]

* Elected: [(${analysis.winners.size() + data.ccpPicks.size()})]
* Candidates: [(${analysis.candidateCount})]
* Total votes: [(${analysis.totalVotes})]
* [Least significant rank](/least-significant-rank): [(${analysis.leastSignificantRank})]

[# th:if="${data.urls.size()} > 0"]
## Posts
[# th:each="entry : ${data.urls}"]
* [ [(${entry.key})] ]( [(${entry.value})] )
[/]
[/]

## Elected Candidates

<table>
    <thead>
        <tr>
            <th>Candidate</th>
            <th>Election round</th>
            <th>Votes at election</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="winner : ${analysis.winners}"]
        <tr>
            <td>[(${winner.candidate})]</td>
            <td style="text-align: right">[(${winner.round})]</td>
            <td style="text-align: right">[(${winner.votes != null} ? ${#numbers.formatInteger(winner.votes, 1, 'COMMA')} : '')]</td>
        </tr>
        [/]
        [# th:if="${data.ccpPicks.size()} > 0"]
            [# th:each="winner : ${data.ccpPicks}"]
            <tr>
                <td>[(${winner})]</td>
                <td style="text-align: right">selected by CCP</td>
                <td></td>
            </tr>
            [/]
        [/]
    </tbody>
</table>

[# th:if="${analysis.withdrawnCandidates.size()} > 0"]
## Withdrawn Candidates

<table>
    <thead>
        <tr>
            <th>Candidate</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="name : ${analysis.withdrawnCandidates}"]
        <tr>
            <td>[(${name})]</td>
        </tr>
        [/]
    </tbody>
</table>
[/]

## Eliminated Candidates
<table>
    <thead>
        <tr>
            <th>Candidate</th>
            <th>Elimination round</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="eliminated : ${analysis.eliminations}"]
        <tr>
            <td>[(${eliminated.candidate})]</td>
            <td style="text-align: right">[(${eliminated.round})]</td>
        </tr>
        [/]
    </tbody>
</table>

## Vote Distribution

Number of ballots placing each candidate at each rank.

```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {
    "url": "data/csm[(${analysis.csmNumber})]-rank-distribution.csv",
    "format": {"type": "csv", "parse": {"Order": "number", "Rank": "number", "Votes": "number"}}
  },
  "height": {"step": 18},
  "mark": {"type": "rect", "tooltip": true},
  "encoding": {
    "x": {
      "field": "Rank",
      "type": "ordinal",
      "title": "Rank",
      "sort": {"op": "min", "field": "Rank", "order": "ascending"}
    },
    "y": {
      "field": "Candidate",
      "type": "nominal",
      "title": null,
      "sort": {"op": "min", "field": "Order", "order": "ascending"}
    },
    "color": {
      "field": "Votes",
      "type": "quantitative",
      "title": "Ballots",
      "scale": {"scheme": "blues"}
    },
    "tooltip": [
      {"field": "Candidate", "type": "nominal"},
      {"field": "Rank", "type": "ordinal"},
      {"field": "Votes", "type": "quantitative"}
    ]
  }
}
```

[Source data](data/csm[(${analysis.csmNumber})]-rank-distribution.csv)
