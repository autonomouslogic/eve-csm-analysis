# CSM [(${analysis.csmNumber})]

[# th:if="${data.years != null and data.years != ''}"]* Years: [(${data.years})]
[/]* Method: [First-past-the-post](https://en.wikipedia.org/wiki/First-past-the-post_voting)
* Elected: [(${analysis.winners.size() + data.ccpPicks.size()})]
* Total votes: [(${#numbers.formatInteger(analysis.totalVotes, 1, 'COMMA')})]

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
            <th>Votes</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="winner : ${analysis.winners}"]
        <tr>
            <td>[(${links.html(winner.candidate)})]</td>
            <td style="text-align: right">[(${#numbers.formatDecimal(winner.votes, 1, 'COMMA', 0, 'POINT')})]</td>
        </tr>
        [/]
        [# th:if="${data.ccpPicks.size()} > 0"]
            [# th:each="winner : ${data.ccpPicks}"]
            <tr>
                <td>[(${links.html(winner)})]</td>
                <td></td>
            </tr>
            [/]
        [/]
    </tbody>
</table>
