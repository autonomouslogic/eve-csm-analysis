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

<table>
    <thead>
        <tr>
            <th>Candidate</th>
            <th>Election round</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="winner : ${analysis.winners}"]
        <tr>
            <td>[(${winner.candidate})]</td>
            <td style="text-align: right">[(${winner.round})]</td>
        </tr>
        [/]
        [# th:if="${data.ccpPicks.size()} > 0"]
            [# th:each="winner : ${data.ccpPicks}"]
            <tr>
                <td>[(${winner})]</td>
                <td style="text-align: right">selected by CCP</td>
            </tr>
            [/]
        [/]
    </tbody>
</table>

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
