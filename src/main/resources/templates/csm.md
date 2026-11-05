# CSM [(${analysis.csmNumber})]

[# th:if="${data.years != null}"]* Years: [(${data.years})]
[/]* Method: [Wright single transferable vote](https://en.wikipedia.org/wiki/Wright_system)
* Elected: [(${analysis.winners.size() + data.ccpPicks.size()})]
* Candidates: [(${analysis.candidateCount})]
* Total votes: [(${#numbers.formatInteger(analysis.totalVotes, 1, 'COMMA')})]
* [Ballots without a winner](/glossary#ballots-without-a-winner): [(${#numbers.formatInteger(analysis.ballotsWithoutWinner, 1, 'COMMA')})] ([(${#numbers.formatPercent(1.0 * analysis.ballotsWithoutWinner / analysis.totalVotes, 1, 1)})])
* [Least significant rank](/glossary#least-significant-rank): [(${analysis.leastSignificantRank})]

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
            <th><a href="/glossary#votes-at-election">Votes at election</a></th>
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

[# th:if="${data.resigned.size() + data.replacements.size()} > 0"]
## Resigned and Replacements

<table>
    <thead>
        <tr>
            <th>Candidate</th>
            <th>Change</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="name : ${data.resigned}"]
        <tr>
            <td>[(${name})]</td>
            <td>resigned</td>
        </tr>
        [/]
        [# th:each="name : ${data.replacements}"]
        <tr>
            <td>[(${name})]</td>
            <td>replacement</td>
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

## Candidate Statistics

<table>
    <thead>
        <tr>
            <th>Candidate</th>
            <th><a href="/glossary#ballots">Ballots</a></th>
            <th><a href="/glossary#ballots">Ballots %</a></th>
            <th><a href="/glossary#first-choice">First choice</a></th>
            <th><a href="/glossary#first-choice">First choice %</a></th>
            <th><a href="/glossary#mean-rank">Mean rank</a></th>
            <th><a href="/glossary#median-rank">Median rank</a></th>
        </tr>
    </thead>
    <tbody>
        [# th:each="stats : ${analysis.candidateStatistics}"]
        <tr[(${stats.elected} ? ' class="elected"' : '')]>
            <td>[(${stats.candidate})]</td>
            <td style="text-align: right">[(${#numbers.formatInteger(stats.ballots, 1, 'COMMA')})]</td>
            <td style="text-align: right">[(${#numbers.formatPercent(1.0 * stats.ballots / analysis.totalVotes, 1, 1)})]</td>
            <td style="text-align: right">[(${#numbers.formatInteger(stats.firstChoiceBallots, 1, 'COMMA')})]</td>
            <td style="text-align: right">[(${#numbers.formatPercent(1.0 * stats.firstChoiceBallots / analysis.totalVotes, 1, 1)})]</td>
            <td style="text-align: right">[(${stats.meanRank != null} ? ${#numbers.formatDecimal(stats.meanRank, 1, 'COMMA', 2, 'POINT')} : '')]</td>
            <td style="text-align: right">[(${stats.medianRank != null} ? ${#numbers.formatDecimal(stats.medianRank, 1, 'COMMA', 1, 'POINT')} : '')]</td>
        </tr>
        [/]
    </tbody>
</table>

## Ballot Sizes

Number of ballots defining each number of ranks.

```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {
    "url": "data/csm[(${analysis.csmNumber})]-ballot-sizes.csv",
    "format": {"type": "csv", "parse": {"Ranks": "number", "Ballots": "number"}}
  },
  "mark": {"type": "bar", "tooltip": true},
  "encoding": {
    "x": {
      "field": "Ranks",
      "type": "ordinal",
      "title": "Ranks defined",
      "sort": {"op": "min", "field": "Ranks", "order": "ascending"}
    },
    "y": {"field": "Ballots", "type": "quantitative", "title": "Ballots"},
    "tooltip": [
      {"field": "Ranks", "type": "ordinal"},
      {"field": "Ballots", "type": "quantitative"}
    ]
  }
}
```

[Source data](data/csm[(${analysis.csmNumber})]-ballot-sizes.csv)

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
          "scale": {"type": "sqrt", "zero": true, "range": ["#ffffff", "#08306b"], "interpolate": "lab"}
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

## Candidate Overlap

How candidates appeared on ballots together. See [Jaccard index](/glossary#jaccard-index).

```vegalite
{
  "$schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "data": {
    "url": "data/csm[(${analysis.csmNumber})]-jaccard.csv",
    "format": {"type": "csv", "parse": {"Order": "number", "OtherOrder": "number", "Jaccard": "number"}}
  },
  "width": {"step": 18},
  "height": {"step": 18},
  "layer": [
    {
      "mark": {"type": "rect", "tooltip": true},
      "encoding": {
        "y": {
          "field": "Candidate",
          "type": "nominal",
          "title": null,
          "sort": {"op": "min", "field": "Order", "order": "ascending"}
        },
        "x": {
          "field": "Other",
          "type": "nominal",
          "title": null,
          "axis": {"labelAngle": -90},
          "sort": {"op": "min", "field": "OtherOrder", "order": "ascending"}
        },
        "color": {
          "field": "Jaccard",
          "type": "quantitative",
          "title": "Jaccard index",
          "scale": {"domain": [0, 1], "type": "sqrt", "range": ["#ffffff", "#08306b"], "interpolate": "lab"}
        },
        "tooltip": [
          {"field": "Candidate", "type": "nominal"},
          {"field": "Other", "type": "nominal", "title": "Compared with"},
          {"field": "Jaccard", "type": "quantitative", "format": ".3f"}
        ]
      }
    },
    {
      "data": {"values": [{}]},
      "mark": {"type": "rect", "fill": null, "stroke": "#d62728", "strokeWidth": 2, "tooltip": false},
      "encoding": {
        "x": {"value": 0},
        "x2": {"value": [(${analysis.electedCount * 18})]},
        "y": {"value": 0},
        "y2": {"value": [(${analysis.electedCount * 18})]}
      }
    }
  ]
}
```

[Source data](data/csm[(${analysis.csmNumber})]-jaccard.csv)

## Popular Ballots

<div class="popular-ballots">
        [# th:each="ballot : ${analysis.popularBallots}"]
    <div class="popular-ballot">
        <strong>[(${#numbers.formatInteger(ballot.ballots, 1, 'COMMA')})] ballots</strong>
        <ol>
            [# th:each="candidate : ${ballot.candidates}"]
            <li>[(${candidate})]</li>
            [/]
        </ol>
    </div>
        [/]
</div>
