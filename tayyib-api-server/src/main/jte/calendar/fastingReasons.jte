@import com.github.edwgiz.tayyib.domain.model.I18n.Fasting.Reason
@import com.github.edwgiz.tayyib.domain.model.I18n.Fasting.FastingReasonGroup
@import java.util.Map


@param FastingReasonGroup reasonGroup
@param Map<String, String> hadithCollectionReferencing

@if(reasonGroup.title() != null)
    <h3 class="fasting-reason-group">${reasonGroup.title()}</h3>
@endif

@for(var reason : reasonGroup.reasons())
    <section id="${reason.id()}" class="fasting-reason">
        @if(reason.title() != null)
            <h4>${reason.title()}</h4>
        @endif

        <ul>
            @for(var cite : reason.cites())
                <li>
                    ${cite.intro()}
                    <q>${cite.quote()}</q>

                    @for(var reference : cite.references())
                        <blockquote>
                            <a
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    href="https://sunnah.com/${reference.collection()}:${reference.hadith()}">

                                ${hadithCollectionReferencing.get(reference.collection())}
                                ${reference.hadith()}
                            </a>
                        </blockquote>
                    @endfor
                </li>
            @endfor
        </ul>
    </section>
@endfor