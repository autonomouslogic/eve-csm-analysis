# Alumni

* &#x1F7E2; = Elected
* &#x1F535; = CCP pick

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
                        [# th:if="${ alumnus.value.elections.get(csm) } == 'picked'"]
                           &#x1F535;
                        [/]
                    [/]
                </td>
            [/]
        </tr>
        [/]
    </tbody>
</table>
