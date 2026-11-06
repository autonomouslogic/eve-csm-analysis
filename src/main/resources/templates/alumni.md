# Alumni

* &#x1F7E2; = Elected
* &#x1F535; = CCP pick
* <span style="color: #9e9e9e;">&#x2B24;</span> = Resigned

[View JSON](/data/alumni.json)

<table>
    <thead>
        <tr>
            <th>Alumnus</th>
            [# th:each="csm : ${csmNumbers}"]
                <th style="min-width: 0;">[(${csm})]</th>
            [/]
        </tr>
    </thead>
    <tbody>
        [# th:each="alumnus : ${alumni.alumni}"]
        <tr>
            <td>[(${alumnus.key})]</td>
            [# th:each="csm : ${csmNumbers}"]
                <td>
                    [# th:if="${ alumnus.value.elections.containsKey(csm) }"]
                        [# th:if="${ alumnus.value.elections.get(csm) } == 'elected'"]
                            &#x1F7E2;
                        [/]
                        [# th:if="${ alumnus.value.elections.get(csm) } == 'replacement'"]
                            &#x1F7E2;
                        [/]
                        [# th:if="${ alumnus.value.elections.get(csm) } == 'picked'"]
                           &#x1F535;
                        [/]
                        [# th:if="${ alumnus.value.elections.get(csm) } == 'resigned'"]
                           <span style="color: #9e9e9e;">&#x2B24;</span>
                        [/]
                    [/]
                </td>
            [/]
        </tr>
        [/]
    </tbody>
</table>

## Most elected

[# th:each="alumnus : ${frequent}"]
1. [(${alumnus.name})]: [(${alumnus.electedCount()})] times
[/]
