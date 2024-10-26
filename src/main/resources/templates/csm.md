# CSM [(${data.csmNumber})]

* Elected: [(${data.winners.size()})]
* Candidates: [(${data.candidateCount})]
* Total votes: [(${data.totalVotes})]
* Least significant rank: [(${data.leastSignificantRank})]

## Elected Candidates

[# th:each="winner : ${data.winners}"]
* [(${winner.candidate})] - elected in round [(${winner.round})]
[/]

## Eliminated Candidates
[# th:each="eliminated : ${data.eliminations}"]
* [(${eliminated.candidate})] - eliminated in round [(${eliminated.round})]
[/]
