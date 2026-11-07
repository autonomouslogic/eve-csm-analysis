# [(${alumnus.name})]

<table>
    <thead>
        <tr>
            <th>Election</th>
            <th>Result</th>
        </tr>
    </thead>
    <tbody>
        [# th:each="run : ${alumnus.runs}"]
        <tr>
            <td><a href="/csm[(${run.key})]/">CSM [(${run.key})]</a></td>
            <td>
                [# th:if="${ run.value } == 'elected'"]&#x1F7E2; Elected[/]
                [# th:if="${ run.value } == 'replacement'"]&#x1F7E2; Replacement[/]
                [# th:if="${ run.value } == 'picked'"]&#x1F535; CCP pick[/]
                [# th:if="${ run.value } == 'resigned'"]<span style="color: #9e9e9e;">&#x2B24;</span> Resigned[/]
                [# th:if="${ run.value } == 'eliminated'"]Eliminated[/]
                [# th:if="${ run.value } == 'withdrawn'"]Withdrawn[/]
            </td>
        </tr>
        [/]
    </tbody>
</table>
