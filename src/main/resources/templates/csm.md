# CSM [(${analysis.csmNumber})]

* Elected: [(${analysis.winners.size() + data.ccpPicks.size()})]
* Candidates: [(${analysis.candidateCount})]
* Total votes: [(${analysis.totalVotes})]
* Least significant rank: [(${analysis.leastSignificantRank})]

[# th:if="${data.urls.size()} > 0"]
## Posts
[# th:each="entry : ${data.urls}"]
* [ [(${entry.key})] ]( [(${entry.value})] )
[/]
[/]

## Elected Candidates

[# th:each="winner : ${analysis.winners}"]
* [(${winner.candidate})] - elected in round [(${winner.round})]
[/]
[# th:if="${data.ccpPicks.size()} > 0"]
[# th:each="winner : ${data.ccpPicks}"]
* [(${winner})] - selected by CCP 
[/]
[/]

## Eliminated Candidates
[# th:each="eliminated : ${analysis.eliminations}"]
* [(${eliminated.candidate})] - eliminated in round [(${eliminated.round})]
[/]
