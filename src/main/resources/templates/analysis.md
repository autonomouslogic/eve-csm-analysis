# CSM [(${data.csmNumber})]

* Candidates: [(${data.candidateCount})]
* Total votes: [(${data.totalVotes})]
* Least significant rank: [(${data.leastSignificantRank})]

## Candidate list

[# th:each="winner : ${data.winners}"]
  * **[(${winner.candidate})]** - elected in round [(${winner.round})]
[/]
[# th:each="eliminated : ${data.eliminations}"]
  * [(${eliminated.candidate})] - eliminated in round [(${eliminated.round})]
[/]
