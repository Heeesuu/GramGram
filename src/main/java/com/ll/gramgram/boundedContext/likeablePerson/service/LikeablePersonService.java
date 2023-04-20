package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final int MaxLikeablePersonCount = 10;


    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {

        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        Long fromInstaMemberId = member.getInstaMember().getId();

        if (likeablePersonRepository.countByFromInstaMemberId(fromInstaMemberId) >= MaxLikeablePersonCount) {
            return RsData.of("F-3", "호감상대는 최대 "+ MaxLikeablePersonCount +"명까지만 등록 할 수 있습니다.");
        }

        Optional<LikeablePerson> existingLikeablePersonAndType = likeablePersonRepository
                .findByToInstaMemberUsername(username)
                .filter(p -> p.getAttractiveTypeCode() == attractiveTypeCode);

        if (existingLikeablePersonAndType.isPresent()) {
            return RsData.of("F-4", "이미 등록된 ID입니다.");
        }

        Optional<LikeablePerson> existingLikeablePerson = likeablePersonRepository
                .findByToInstaMemberUsername(username)
                .filter(p -> p.getAttractiveTypeCode() != attractiveTypeCode);

        if (existingLikeablePerson.isPresent()) {
            existingLikeablePerson.get().setAttractiveTypeCode(attractiveTypeCode);
            likeablePersonRepository.save(existingLikeablePerson.get());
            return RsData.of("S-2", "인스타 유저(%s)의 매력포인트가 수정되었습니다.".formatted(username));
        }


        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

            LikeablePerson likeablePerson = LikeablePerson
                    .builder()
                    .fromInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
                    .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                    .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                    .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                    .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                    .build();

            likeablePersonRepository.save(likeablePerson); // 저장

            return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
        }


    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeablePerson> findById(Integer id) {
        return likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData<LikeablePerson> delete(Member member, Integer id){
        Optional<LikeablePerson> opLikeablePerson = likeablePersonRepository.findById(id);

        if (opLikeablePerson.isEmpty()) {
            return RsData.of("F-1", "삭제할 호감상대가 존재하지 않습니다.");
        }

        LikeablePerson likeablePerson = opLikeablePerson.get();

        if(!likeablePerson.getFromInstaMember().equals(member.getInstaMember())){
            return RsData.of("F-1", "해당 호감상대를 삭제할 수 없습니다.");
        }

        String toInstaMemberUsername = likeablePerson.getToInstaMember().getUsername();
        likeablePersonRepository.deleteById(id);

        return RsData.of("S-1", "인스타그램 유저(%s)를 호감상대에서 삭제하였습니다.".formatted(toInstaMemberUsername), likeablePerson);
    }





}
