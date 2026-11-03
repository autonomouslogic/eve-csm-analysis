# CSM [(${analysis.csmNumber})]

* Elected: [(${analysis.winners.size() + data.ccpPicks.size()})]
* Candidates: [(${analysis.candidateCount})]
* Total votes: [(${analysis.totalVotes})]
* [Ballots without a winner](/ballots-without-a-winner): [(${analysis.ballotsWithoutWinner})] ([(${#numbers.formatPercent(1.0 * analysis.ballotsWithoutWinner / analysis.totalVotes, 1, 1)})])
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
            <th><a href="/votes-at-election">Votes at election</a></th>
        </tr>
    </thead>
    <tbody>
        [# th:each="winner : ${analysis.winners}"]
        <tr>
            <td>[(${winner.candidate})]</td>
            <td style="text-align: right">[(${winner.round})]</td>
            <td style="text-align: right">[(${winner.votes != null} ? ${#numbers.formatDecimal(winner.votes, 1, 'COMMA', 1, 'POINT')} : '')]</td>
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
            <th>Votes at elimination</th>
            <th>Votes short of quota</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="eliminated : ${analysis.eliminations}"]
        <tr>
            <td>[(${eliminated.candidate})]</td>
            <td style="text-align: right">[(${eliminated.round})]</td>
            <td style="text-align: right">[(${#numbers.formatDecimal(eliminated.votes, 1, 'COMMA', 1, 'POINT')})]</td>
            <td style="text-align: right">[(${#numbers.formatDecimal(eliminated.votesShortOfQuota, 1, 'COMMA', 1, 'POINT')})]</td>
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
  "encoding": {
    "y": {
      "field": "Candidate",
      "type": "nominal",
      "title": null,
      "sort": {"op": "min", "field": "Order", "order": "ascending"}
    }
  },
  "layer": [
    {
      "mark": {"type": "rect", "tooltip": true},
      "encoding": {
        "x": {
          "field": "Rank",
          "type": "ordinal",
          "title": "Rank",
          "sort": {"op": "min", "field": "Rank", "order": "ascending"}
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
    },
    {
      "mark": {"type": "rule", "color": "#d62728", "strokeWidth": 2},
      "encoding": {
        "y": {
          "field": "Candidate",
          "type": "nominal",
          "bandPosition": 1,
          "sort": {"op": "min", "field": "Order", "order": "ascending"}
        },
        "opacity": {
          "condition": {"test": "datum.Rank == 1 && datum.Order == [(${analysis.winners.size()})]", "value": 1},
          "value": 0
        }
      }
    }
  ]
}
```

[Source data](data/csm[(${analysis.csmNumber})]-rank-distribution.csv)
