# CSM [(${data.csmNumber})]

* Elected: [(${data.winners.size()})]
* Candidates: [(${data.candidateCount})]
* Total votes: [(${data.totalVotes})]
* Least significant rank: [(${data.leastSignificantRank})]

## Candidates

[# th:each="winner : ${data.winners}"]  * <b>[(${winner.candidate})]</b> - elected in round [(${winner.round})]
[/][# th:each="eliminated : ${data.eliminations}"]  * [(${eliminated.candidate})] - eliminated in round [(${eliminated.round})]
[/]
