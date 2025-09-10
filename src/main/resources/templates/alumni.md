# Alumni

* p = CCP pick
* e = Elected

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
                <td>[( ${ alumnus.value.elections.get(csm) } )]</td>
            [/]
        </tr>
        [/]
    </tbody>
</table>
